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
*  File        : DocumentBasicTempl.java
*  Modified    :
*    2001-02-27  ShDilk  ASP2JAVA Tool - Created Using the ASP file DocumentBasicTempl.asp
*    2001-05-24  ShDilk  Call Id:65345
*    2002-12-30  DiKalk  2002-2 SP3 Merge:
*                        2002-09-24  Dinklk  Bug 31419, Modified getDescription and getTitle functions to make window title and heading
*                                            as "Basic Data - Classes, Groups and Approval Templates" (earlier it was as "Basic Data")
*                        2002-09-31  Larelk  Bug 31903 Increased the field length in person_id
*                        2003-02-26  MDAHSE  Changed getDescription() and getTitle()
*   2003-08-01   NiSilk  Fixed call 95769, modified method run() and added method adjust().
*   2003-08-29   InoSlk  Call ID 101731: Modified doReset() and clone().
*   2003-10-15   NiSilk  Call ID 106866: Set ITEM4_DESCRIPTION mandatory.
*   2003-10-18   NiSilk  Call Id 108040: Modified methods validateItem4PersonId and validateItem4GroupId.
*   2003-10-18   InoSlk  Call ID 108043: Modified Javascript methods validateItem6PersonId(),validateItem6GroupId().
*   2003-10-18   DIKALK  Call ID 107500: Removed call to mgr.generateHeadTag() in printContents()
*   2004-02-17   BAKALK  Replaced document.ClientUtil.connect with __connect.
*   2004-03-08   BAKALK  Changed the layout in "copy document group". Fixed the validation problem in that dialog. Found another problem that "copy approval template"
*                        is shown even in other tabs than "Approval Template".Fixed it too.
*   2004-03-17   BAKALK  Changed the layout in "Approval Template" dialog. Removed the minimize button in "copy document group".
*   2004-05-13   BAKALK  Merged the bug 44122.
*   2004-07-29   DIKALK  Merged Bud Id 45195
*   2005-03-09   SUKMLK  Merged Bug ID 47773
*   2005-10-24   NISILK  APMR414-Electronic Signature, Added ITEM3_SECURITY_CHECKPOINT_REQ.
*   2006-02-06   ThWilk  Call ID 133008, Modified saveReturn0()
*   2006-07-25   BAKALK  Bug ID 58216, Fixed Sql Injection.
*   2006-09-06   NIJALK  Bug 57781, Modified transfer().
*   2006-12-14   KARALK   DMPR303 NEW COLUMN ADMIN_ACCESS
*   2007-02-15   WYRALK   Merged Bug ID 62227
*   2007-08-06   UPDELK  Bug 146930 : Renamed Document Group Tab to Person Group 
*   2007-08-15   ASSALK  Merged Bug 58526, Modified printContents().   
*   2008-03-03   VIRALK   Bug Id 69651 Replaced view PERSON_INFO with PERSON_INFO_LOV.
*   2008-10-15   SHTHLK  Bug Id 70286, Replaced addCustomCommand() with addSecureCustomCommand()
*   2009-07-31   SHTHLK  Bug Id 84299, Set the length of ITEM8_DESCRIPTION to 40 in printcontent
*   2009-09-18   SHTHLK   Bug Id 85876, Added warnings when coping approval templates without steps
*   2009-09-29   SHTHLK   Bug Id 85876, Used Steps_Exists() instead of Get_Max_Line_No__()
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocumentBasicTempl extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocumentBasicTempl");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext ctx;
   private ASPHTMLFormatter fmt;
   private ASPForm frm;
   private ASPLog log;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
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
   private ASPBlock itemblk5;
   private ASPRowSet itemset5;
   private ASPCommandBar itembar5;
   private ASPTable itemtbl5;
   private ASPBlockLayout itemlay5;
   private ASPBlock itemblk6;
   private ASPRowSet itemset6;
   private ASPCommandBar itembar6;
   private ASPTable itemtbl6;
   private ASPBlockLayout itemlay6;
   private ASPBlock itemblk7;
   private ASPRowSet itemset7;
   private ASPCommandBar itembar7;
   private ASPTable itemtbl7;
   private ASPBlockLayout itemlay7;
   private ASPBlock itemblk8;
   private ASPRowSet itemset8;
   private ASPCommandBar itembar8;
   private ASPTable itemtbl8;
   private ASPBlockLayout itemlay8;
   private ASPTabContainer tabs;
   private ASPBlock itemdummy;
   private ASPField f;
   
   private ASPBlock doc_sub_class_blk;
   private ASPRowSet doc_sub_class_set;
   private ASPCommandBar doc_sub_class_bar;
   private ASPTable doc_sub_class_tbl;
   private ASPBlockLayout doc_sub_class_lay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private String bothCannotHaveValues;
   private boolean showDialog;
   private boolean showTmplDialog;
   private String activetab;
   private String val;
   private ASPCommand cmd;
   private String personName;
   private String userID;
   private String grpDes;
   private String profDes;
   private ASPQuery q;
   private ASPBuffer data;
   private int currrow;
   private String masterGroup;
   private String masterProfile;
   private int headrowno;
   private String grp_id;
   private String doc_cls;
   private String item7_group_id;
   private String item7_group_description;
   private String item8_profile_id;
   private String item8_description;
   private String txt;


   //===============================================================
   // Construction
   //===============================================================
   public DocumentBasicTempl(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      bothCannotHaveValues   = null;
      showDialog   = false;
      showTmplDialog   = false;
      activetab   = null;
      val   = null;
      cmd   = null;
      personName   = null;
      userID   = null;
      grpDes   = null;
      profDes   = null;
      q   = null;
      data   = null;
      currrow   = 0;
      masterGroup = null;
      masterProfile = null;
      headrowno   = 0;
      grp_id   = null;
      doc_cls   = null;
      item7_group_id   = null;
      item7_group_description   = null;
      item8_profile_id   = null;
      item8_description   = null;
      txt   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocumentBasicTempl page = (DocumentBasicTempl)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.bothCannotHaveValues   = null;
      page.showDialog   = false;
      page.showTmplDialog   = false;
      page.activetab   = null;
      page.val   = null;
      page.cmd   = null;
      page.personName   = null;
      page.userID   = null;
      page.grpDes   = null;
      page.profDes   = null;
      page.q   = null;
      page.data   = null;
      page.currrow   = 0;
      page.masterGroup = null;
      page.masterProfile = null;
      page.headrowno   = 0;
      page.grp_id   = null;
      page.doc_cls   = null;
      page.item7_group_id   = null;
      page.item7_group_description   = null;
      page.item8_profile_id   = null;
      page.item8_description   = null;
      page.txt   = null;

      // Cloning immutable attributes
      page.ctx = page.getASPContext();
      page.fmt = page.getASPHTMLFormatter();
      page.frm = page.getASPForm();
      page.log = page.getASPLog();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
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
      page.itemblk3 = page.getASPBlock(itemblk3.getName());
      page.itemset3 = page.itemblk3.getASPRowSet();
      page.itembar3 = page.itemblk3.getASPCommandBar();
      page.itemtbl3 = page.getASPTable(itemtbl3.getName());
      page.itemlay3 = page.itemblk3.getASPBlockLayout();
      page.itemblk4 = page.getASPBlock(itemblk4.getName());
      page.itemset4 = page.itemblk4.getASPRowSet();
      page.itembar4 = page.itemblk4.getASPCommandBar();
      page.itemtbl4 = page.getASPTable(itemtbl4.getName());
      page.itemlay4 = page.itemblk4.getASPBlockLayout();
      page.itemblk5 = page.getASPBlock(itemblk5.getName());
      page.itemset5 = page.itemblk5.getASPRowSet();
      page.itembar5 = page.itemblk5.getASPCommandBar();
      page.itemtbl5 = page.getASPTable(itemtbl5.getName());
      page.itemlay5 = page.itemblk5.getASPBlockLayout();
      page.itemblk6 = page.getASPBlock(itemblk6.getName());
      page.itemset6 = page.itemblk6.getASPRowSet();
      page.itembar6 = page.itemblk6.getASPCommandBar();
      page.itemtbl6 = page.getASPTable(itemtbl6.getName());
      page.itemlay6 = page.itemblk6.getASPBlockLayout();
      page.itemblk7 = page.getASPBlock(itemblk7.getName());
      page.itemset7 = page.itemblk7.getASPRowSet();
      page.itembar7 = page.itemblk7.getASPCommandBar();
      page.itemtbl7 = page.getASPTable(itemtbl7.getName());
      page.itemlay7 = page.itemblk7.getASPBlockLayout();
      page.itemblk8 = page.getASPBlock(itemblk8.getName());
      page.itemset8 = page.itemblk8.getASPRowSet();
      page.itembar8 = page.itemblk8.getASPCommandBar();
      page.itemtbl8 = page.getASPTable(itemtbl8.getName());
      page.itemlay8 = page.itemblk8.getASPBlockLayout();
      page.tabs = page.getASPTabContainer();
      page.itemdummy = page.getASPBlock(itemdummy.getName());
      page.f = page.getASPField(f.getName());


      return page;
   }

   public void adjust()
   {
      if (tabs.getActiveTab()==4)
         if (itemset5.countRows()==0)
         {
            itemlay5.setLayoutMode (itemlay5.FIND_LAYOUT);
         }
   }
   public void run()
   {
      ASPManager mgr = getASPManager();

      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      fmt = mgr.newASPHTMLFormatter();
      frm = mgr.getASPForm();
      log = mgr.getASPLog();

      bothCannotHaveValues = mgr.translate("DOCMAWDOCUMENTBASICTEMPLBOTHCANNOTHAVEVALUES: Both Group ID and Person ID cannot have values.");

      showDialog = ctx.readFlag ("SHOWDLG",false);
      showTmplDialog = ctx.readFlag ("SHOWTMPLDLG",false);
      masterGroup = ctx.readValue("MASTERGROUP","");
      masterProfile = ctx.readValue("MASTERPROFILE","");


      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if ((!mgr.isEmpty(mgr.getQueryStringValue("GROUP_ID"))))
         transfer();
      else if (mgr.buttonPressed("OK"))
         copyGroupMembers();
      else if (mgr.buttonPressed("CANCEL"))
         showDialog = false;
      else if (mgr.buttonPressed("OK2"))
         copyApprTempl();
      else if (mgr.buttonPressed("CANCEL2"))
         showTmplDialog = false;
      else 
         okFindITEM0();    

      ctx.writeFlag ("SHOWDLG",showDialog);
      ctx.writeFlag ("SHOWTMPLDLG",showTmplDialog);
      ctx.writeValue("MASTERGROUP",masterGroup);
      ctx.writeValue("MASTERPROFILE",masterProfile);
      tabs.saveActiveTab();
      adjust();
   }

   //=============================================================================
   //  Validation
   //=============================================================================

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");

      if ("ITEM2_PERSON_ID".equals(val))
      {
         cmd = trans.addCustomFunction("PERNAME","PERSON_INFO_API.Get_Name","ITEM2_NAME");
         cmd.addParameter("ITEM2_PERSON_ID");
         cmd = trans.addCustomFunction("UID","PERSON_INFO_API.Get_User_Id","ITEM2_USER_ID");
         cmd.addParameter("ITEM2_PERSON_ID");
         cmd = trans.addCustomFunction("PERSONDEPT", "GENERAL_ORG_POS_PERSON_API.Get_Person_Def_Dept", "ITEM2_PERSON_DEPT");
         cmd.addParameter("ITEM2_PERSON_ID");
         cmd = trans.addCustomFunction("PERSONDEPTNAME", "GENERAL_ORG_POS_PERSON_API.Get_Person_Def_Dept_Desc", "ITEM2_PERSON_DEPT_NAME");
         cmd.addParameter("ITEM2_PERSON_ID");
         
         trans = mgr.validate(trans);
         personName = trans.getValue("PERNAME/DATA/NAME");
         userID = trans.getValue("UID/DATA/USER_ID");
         String person_dept = mgr.isEmpty(trans.getValue("PERSONDEPT/DATA/ITEM2_PERSON_DEPT")) ? "" : trans.getValue("PERSONDEPT/DATA/ITEM2_PERSON_DEPT");
         String person_dept_name = mgr.isEmpty(trans.getValue("PERSONDEPTNAME/DATA/ITEM2_PERSON_DEPT_NAME")) ? "" : trans.getValue("PERSONDEPTNAME/DATA/ITEM2_PERSON_DEPT_NAME");
         trans.clear();
         txt = (mgr.isEmpty(personName) ? "" : personName) + "^" + (mgr.isEmpty(userID) ? "" : userID) + "^" + person_dept + "^" + person_dept_name + "^" ;
         mgr.responseWrite(txt);
      }

      else if ("ITEM4_PERSON_ID".equals(val))
      {
         cmd = trans.addCustomFunction("PERNAME","PERSON_INFO_API.Get_Name","ITEM4_NAME");
         cmd.addParameter("ITEM4_PERSON_ID");
         trans = mgr.validate(trans);
         personName = trans.getValue("PERNAME/DATA/ITEM4_NAME");
         trans.clear();

         //    Set ITEM4_GROUP_ID and ITEM4_GROUP_DESCRIPTION to null.
         //    Modified to disable automatic clearing of ITEM4_GROUP_ID, and ITEM4_GROUP_DESCRIPTION
         //      txt = (mgr.isEmpty(personName) ? "" : personName) + "^" + "" + "^" + "" + "^";
         txt = (mgr.isEmpty(personName) ? "" : personName) + "^";
         mgr.responseWrite(txt);
      }

      else if ("ITEM4_GROUP_ID".equals(val))
      {
         cmd = trans.addCustomFunction("GRPDES","DOCUMENT_GROUP_API.Get_Group_Description","ITEM4_GROUP_DESCRIPTION");
         cmd.addParameter("ITEM4_GROUP_ID");
         trans = mgr.validate(trans);
         grpDes = trans.getValue("GRPDES/DATA/ITEM4_GROUP_DESCRIPTION");

         //    Set ITEM4_PERSON_ID and ITEM4_NAME to null.
         //    Modified to disable automatic clearing of ITEM4_PERSON_ID, and ITEM4_NAME
         //      txt = (mgr.isEmpty(grpDes) ? "" : grpDes) + "^" + "" + "^" + "" + "^";
         txt = (mgr.isEmpty(grpDes) ? "" : grpDes) + "^";
         mgr.responseWrite(txt);
      }

      else if ("ITEM6_GROUP_ID".equals(val))
      {
         cmd = trans.addCustomFunction("GRPDES","DOCUMENT_GROUP_API.Get_Group_Description","ITEM6_GROUP_DESCRIPTION");
         cmd.addParameter("ITEM6_GROUP_ID");
         trans = mgr.validate(trans);
         grpDes = trans.getValue("GRPDES/DATA/ITEM6_GROUP_DESCRIPTION");

         //    Set ITEM6_PERSON_ID, ITEM6_NAME and ITEM6_USER_ID to null.
         //    Modified to disable automatic clearing of ITEM6_PERSON_ID, ITEM6_NAME, and ITEM6_USER_ID
         //      txt = (mgr.isEmpty(grpDes) ? "" : grpDes) + "^" + "" + "^" + "" + "^" + "" + "^";
         txt = (mgr.isEmpty(grpDes) ? "" : grpDes) + "^";
         mgr.responseWrite(txt);
      }

      else if ("ITEM6_PERSON_ID".equals(val))
      {
         cmd = trans.addCustomFunction("PERNAME","PERSON_INFO_API.Get_Name","ITEM6_NAME");
         cmd.addParameter("ITEM6_PERSON_ID");
         trans = mgr.validate(trans);
         personName = trans.getValue("PERNAME/DATA/ITEM6_NAME");
         trans.clear();

         cmd = trans.addCustomFunction("UID","PERSON_INFO_API.Get_User_Id","ITEM6_USER_ID");
         cmd.addParameter("ITEM6_PERSON_ID");
         trans = mgr.validate(trans);
         userID = trans.getValue("UID/DATA/ITEM6_USER_ID");
         trans.clear();

         //    Set ITEM6_GROUP_ID and ITEM6_GROUP_DESCRIPTION to null.
         //      txt = (mgr.isEmpty(personName) ? "" : personName) + "^" + (mgr.isEmpty(userID) ? "" : userID) + "^" + "" + "^" + "" + "^";
         txt = (mgr.isEmpty(personName) ? "" : personName) + "^" + (mgr.isEmpty(userID) ? "" : userID) + "^";
         mgr.responseWrite(txt);
      }

      else if ("ITEM7_GROUP_ID".equals(val))
      {
         cmd = trans.addCustomFunction("GRPDES","DOCUMENT_GROUP_API.Get_Group_Description","ITEM7_GROUP_DESCRIPTION");
         cmd.addParameter("ITEM7_GROUP_ID");
         trans = mgr.validate(trans);
         grpDes = trans.getValue("GRPDES/DATA/GROUP_DESCRIPTION");

         txt = (mgr.isEmpty(grpDes) ? "" : grpDes+"^");
         mgr.responseWrite(txt);
      }

      else if ("ITEM8_PROFILE_ID".equals(val))
      {
         cmd = trans.addCustomFunction("PROFDES","APPROVAL_PROFILE_API.Get_Description","ITEM8_DESCRIPTION");
         cmd.addParameter("ITEM8_PROFILE_ID",mgr.readValue("ITEM8_PROFILE_ID"));
         trans = mgr.validate(trans);
         profDes = trans.getValue("PROFDES/DATA/DESCRIPTION");

         txt = (mgr.isEmpty(profDes) ? "" : profDes+"^");
         mgr.responseWrite(txt);
      }
      mgr.endResponse();
   }

   //=============================================================================
   //  Command functions
   //=============================================================================

   public void  transfer()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addEmptyQuery(itemblk1);
      //bug 58216 starts
      q.addWhereCondition("GROUP_ID = ?");
      //Bug 57781, Start, Modified parameter
      q.addParameter("ITEM1_GROUP_ID",mgr.readValue("GROUP_ID"));
      //Bug 57781, End
      //bug 58216 end

      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk1);

      if (itemset1.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICTEMPLNODATA: No data found."));
         itemset1.clear();
      }

      //Bug 57781, Start
      if (itemset1.countRows() == 1)
      {
          itemlay1.setLayoutMode(itemlay1.SINGLE_LAYOUT);
          activateDocGroup();
      }
      //Bug 57781, End

      eval(itemset1.syncItemSets());

      if (itemset1.countRows() > 0)
         okFindITEM2();
   }

   //====================== Functions for Document Class =========================
   //====================== ITEM0 ====================
   // ITEM0

   public void  okFindITEM0()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk0);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk0);

      if (itemset0.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICTEMPLNODATAFOUND: No data found"));
         itemset0.clear();
      }
      else
         okFindITEMSUB();

   }


   public void  newRowITEM0()
   {
      ASPManager mgr = getASPManager();


      trans.clear();
      cmd = trans.addEmptyCommand("ITEM0","DOC_CLASS_API.New__",itemblk0);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM0/DATA");
      itemset0.addRow(data);
   }


   public void  countFindITEM0()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk0);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk0);
      itemlay0.setCountValue(toInt(itemset0.getValue("N")));
      itemset0.clear();
   }


   public void  saveReturn0()
   {
      ASPManager mgr = getASPManager();

      currrow = itemset0.getCurrentRowNo();
      itemset0.changeRow();
      itemset0.setValue("OBJVERSION", itemset0.getRow().getValue("OBJVERSION") + (char)30);
      mgr.submit(trans);
      trans.clear();
      itemset0.goTo(currrow);
   }
   
   public void okFindITEMSUB()
   {
      ASPManager mgr = getASPManager();
      
      if (itemset0.countRows() == 0)
         return;
      
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPQuery q;
      int headrowno;

      q = trans.addQuery(doc_sub_class_blk);
      q.addWhereCondition("DOC_CLASS = ?");
      q.addParameter("SUBCLASS_DOC_CLASS", itemset0.getValue("DOC_CLASS"));
      q.includeMeta("ALL");
      headrowno = itemset0.getCurrentRowNo();
      mgr.querySubmit(trans,doc_sub_class_blk);
      itemset0.goTo(headrowno);
   }
   
   public void newRowITEMSUB()
   {
      ASPManager mgr = getASPManager();
      ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();
      ASPCommand cmd;
      ASPBuffer data;

      cmd = trans.addEmptyCommand("SUBCLASS","DOC_SUB_CLASS_API.New__",doc_sub_class_blk);
      cmd.setOption("ACTION","PREPARE");
      cmd.setParameter("SUBCLASS_DOC_CLASS", itemset0.getValue("DOC_CLASS"));
      trans = mgr.perform(trans);
      data = trans.getBuffer("SUBCLASS/DATA");
      doc_sub_class_set.addRow(data);
   }

   //========================= Functions for Document Group =========================
   //========================= ITEM1 (Master) AND ITEM2 (Detail) ====================
   // ITEM1

   public void  okFindITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk1);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk1);

      if (itemset1.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICTEMPLNODATA: No data found."));
         itemset1.clear();
      }
      else
      {
         itemlay1.setLayoutMode(itemlay1.SINGLE_LAYOUT);
         okFindITEM2();
      }
      eval(itemset1.syncItemSets());

   }


   public void SaveNewITEM1()
   {
      SaveITEM1();
      newRowITEM1();
   }

   public void SaveReturnITEM1()
   {
      int rowNo = itemset1.getCurrentRowNo();
      SaveITEM1();
      itemset1.goTo(rowNo);
      itemlay1.setLayoutMode(itemlay1.SINGLE_LAYOUT);

   }


   public void SaveITEM1()
   {
      ASPManager mgr = getASPManager();

      itemset1.changeRow();
      mgr.submit(trans);
      trans.clear();

   }




   public void  countFindITEM1()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk1);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk1);
      itemlay1.setCountValue(toInt(itemset1.getValue("N")));
      itemset1.clear();
   }


   public void  newRowITEM1()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM1","DOCUMENT_GROUP_API.New__",itemblk1);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM1/DATA");
      itemset1.addRow(data);
   }


   public void  cancelNewITEM1()
   {

      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
      okFindITEM1();
   }

   // ITEM2

   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk2);
      //bug 58216 starts
      q.addWhereCondition("GROUP_ID = ?");
      q.addParameter("ITEM1_GROUP_ID",itemset1.getValue("GROUP_ID"));
      //bug 58216 end
      q.includeMeta("ALL");
      headrowno = itemset1.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk2);
      itemlay2.setLayoutMode(itemlay2.MULTIROW_LAYOUT);
      itemset1.goTo(headrowno);
   }


   public void  countFindITEM2()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk2);
      q.setSelectList("to_char(count(*)) N");
      //bug 58216 starts
      q.addWhereCondition("GROUP_ID = ?");
      q.addParameter("ITEM1_GROUP_ID",itemset1.getValue("GROUP_ID"));
      //bug 58216 end
      mgr.querySubmit(trans,itemblk2);
      itemlay2.setCountValue(toInt(itemset2.getValue("N")));
      itemset2.clear();
   }


   public void  newRowITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM2","DOCUMENT_GROUP_MEMBERS_API.New__",itemblk2);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM2/DATA");

      grp_id = itemset1.getRow().getValue("GROUP_ID");
      data.setFieldItem("ITEM2_GROUP_ID",grp_id);

      itemset2.addRow(data);
   }

   //========================= Functions for Approval Template =========================
   //========================= ITEM3 (Master) AND ITEM4 (Detail) ====================
   // ITEM3

   public void  okFindITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk3);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk3);

      if (itemset3.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICTEMPLNODATA: No data found."));
         itemset3.clear();
      }
      else
      {
         itemlay3.setLayoutMode(itemlay3.SINGLE_LAYOUT);
         okFindITEM4();
      }
      eval(itemset3.syncItemSets());

   }


   public void  countFindITEM3()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk3);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk3);
      itemlay3.setCountValue(toInt(itemset3.getValue("N")));
      itemset3.clear();
   }


   public void  newRowITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM3","APPROVAL_PROFILE_API.New__",itemblk3);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM3/DATA");
      itemset3.addRow(data);
   }


   public void  cancelNewITEM3()
   {

      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);
      okFindITEM3();
   }

   // ITEM4

   public void  okFindITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk4);
      //bug 58216 starts
      q.addWhereCondition("PROFILE_ID = ?");
      q.addParameter("ITEM3_PROFILE_ID",itemset3.getValue("PROFILE_ID"));
      //bug 58216 end
      q.setOrderByClause("STEP_NO"); 
      q.includeMeta("ALL");
      headrowno = itemset3.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk4);
      itemlay4.setLayoutMode(itemlay4.MULTIROW_LAYOUT);
      itemset3.goTo(headrowno);
   }


   public void  countFindITEM4()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk4);
      q.setSelectList("to_char(count(*)) N");
      //bug 58216 starts
      q.addWhereCondition("PROFILE_ID = ?");
      q.addParameter("ITEM3_PROFILE_ID",itemset3.getValue("PROFILE_ID"));
      //bug 58216 end
      mgr.querySubmit(trans,itemblk4);
      itemlay4.setCountValue(toInt(itemset4.getValue("N")));
      itemset4.clear();
   }


   public void  newRowITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM4","APPROVAL_TEMPLATE_API.New__",itemblk4);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM4/DATA");

      grp_id = itemset3.getRow().getValue("PROFILE_ID");
      data.setFieldItem("ITEM4_PROFILE_ID",grp_id);

      itemset4.addRow(data);
   }

   //========================= Functions for Access Template =========================
   //========================= ITEM5 (Master) AND ITEM5 (Detail) ====================
   // ITEM5

   public void  okFindITEM5()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk5);
      q.includeMeta("ALL");
      mgr.querySubmit(trans,itemblk5);

      if (itemset5.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICTEMPLNODATA: No data found."));
         itemset5.clear();
      }  
//      else
//      {
//         itemlay5.setLayoutMode(itemlay5.SINGLE_LAYOUT);
//         okFindITEM6();
//      }
      eval(itemset5.syncItemSets());

   }


   public void  countFindITEM5()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk5);
      q.setSelectList("to_char(count(*)) N");
      mgr.querySubmit(trans,itemblk5);
      itemlay5.setCountValue(toInt(itemset5.getValue("N")));
      itemset5.clear();
   }


   public void  newRowITEM5()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM5","DOC_CLASS_API.New__",itemblk5);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM5/DATA");
      itemset5.addRow(data);
   }


   public void  cancelNewITEM5()
   {

      itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);
      okFindITEM5();
   }

   // ITEM6

   public void  okFindITEM6()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(itemblk6);
      //bug 58216 starts
      q.addWhereCondition("DOC_CLASS = ?");
      q.addParameter("ITEM0_DOC_CLASS",itemset5.getValue("DOC_CLASS"));
      //bug 58216 end
      q.includeMeta("ALL");
      headrowno = itemset5.getCurrentRowNo();
      mgr.querySubmit(trans,itemblk6);
      itemlay6.setLayoutMode(itemlay6.MULTIROW_LAYOUT);
      itemset5.goTo(headrowno);
   }


   public void  countFindITEM6()
   {
      ASPManager mgr = getASPManager();

      q = trans.addQuery(itemblk6);
      q.setSelectList("to_char(count(*)) N");
      //bug 58216 starts
      q.addWhereCondition("DOC_CLASS = ?");
      q.addParameter("ITEM0_DOC_CLASS",itemset5.getValue("DOC_CLASS"));
      //bug 58216 end
      mgr.querySubmit(trans,itemblk6);
      itemlay6.setCountValue(toInt(itemset6.getValue("N")));
      itemset6.clear();
   }

   public void cancelFindITEM5()
   {
      debug("calling cancel find");
      ASPManager mgr = getASPManager();
      tabs.setActiveTab(1);
   }

   public void  newRowITEM6()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addEmptyCommand("ITEM6","DOCUMENT_ACCESS_TEMPLATE_API.New__",itemblk6);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("ITEM6/DATA");

      doc_cls = itemset5.getRow().getValue("DOC_CLASS");
      data.setFieldItem("ITEM6_DOC_CLASS",doc_cls);

      itemset6.addRow(data);
   }

   //=============================================================================
   //   CUSTOM FUNCTIONS
   //=============================================================================

   public void  getCopyDialog()
   {

      ASPManager mgr = getASPManager();

      if (itemlay1.isMultirowLayout())
      {
         itemset1.storeSelections();
         itemset1.setFilterOn();
         masterGroup=itemset1.getRow().getValue("GROUP_ID");
         itemset1.setFilterOff();
         itemset1.unselectRow();
      }
      else
      {
         itemset1.selectRow();
         masterGroup=itemset1.getRow().getValue("GROUP_ID");
      }

      showDialog = true;
   }


   public void  getCopyApprTemplDialog()
   {
      ASPManager mgr = getASPManager();
      if (itemlay3.isMultirowLayout())
      {
         itemset3.storeSelections();
         itemset3.setFilterOn();
         masterProfile=itemset3.getRow().getValue("PROFILE_ID");
         itemset3.setFilterOff();
         itemset3.unselectRow();
      }
      else
      {
         itemset3.selectRow();
         masterProfile=itemset3.getRow().getValue("PROFILE_ID");
      }

      showTmplDialog = true;
   }


   public void  copyGroupMembers()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addCustomCommand("COPYGRP","DOCUMENT_GROUP_MEMBERS_API.Copy_Members_Of_Group");
      cmd.addParameter("ITEM7_GROUP_ID", mgr.readValue("ITEM7_GROUP_ID"));
      cmd.addParameter("ITEM1_GROUP_ID", masterGroup);
      trans = mgr.perform(trans);
      trans.clear();

      okFindITEM2();
      showDialog = false;

   }


   public void  copyApprTempl()
   {
      ASPManager mgr = getASPManager();

      cmd = trans.addCustomCommand("COPYTMPL","Approval_Template_API.COPY_TEMPLATE__");
      cmd.addParameter("ITEM8_PROFILE_ID", mgr.readValue("ITEM8_PROFILE_ID"));
      cmd.addParameter("ITEM3_PROFILE_ID", masterProfile);

      //Bug Id 85876, Start
      cmd = trans.addCustomFunction("CHECKCOPYTMPL", "Approval_Template_API.Steps_Exists","ITEMDUMMY_INFO");
      cmd.addParameter("ITEM8_PROFILE_ID",mgr.readValue("ITEM8_PROFILE_ID"));
      //Bug Id 85876, End
      trans = mgr.perform(trans);      
      //Bug Id 85876, Start
      if ("FALSE".equals(trans.getValue("CHECKCOPYTMPL/DATA/ITEMDUMMY_INFO")))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTBASICTEMPLNOUSERS: The selected Approval Template contains no step(s)."));
      }
      //Bug Id 85876, End  
      trans.clear();
      okFindITEM4();
      showTmplDialog = false;

   }

   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   // Tab activate functions
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public void  activateDocClasses()
   {
      tabs.setActiveTab(1);
   }


   public void  activateDocGroup()
   {
      tabs.setActiveTab(2);
      okFindITEM1();
   }


   public void  activateApprovalTempl()
   {
      tabs.setActiveTab(3);
      okFindITEM3();
   }


   public void  activateAccessTempl()
   {
      tabs.setActiveTab(4);
      okFindITEM5();    
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      disableConfiguration();

      headblk = mgr.newASPBlock("HEAD");

      headbar = mgr.newASPCommandBar(headblk);

      headbar.disableCommand(headbar.FIND);
      headbar.disableCommand(headbar.BACK);
      headbar.disableCommand(headbar.FORWARD);
      headbar.disableCommand(headbar.BACKWARD);
      headbar.disableCommand(headbar.NEWROW);

      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);


      // tab commands
      headbar.addCustomCommand("activateDocClasses", mgr.translate("DOCMAWDOCUMENTBASICTEMPLDOCCLASSES1: Document Classes"));
      headbar.addCustomCommand("activateDocGroup", mgr.translate("DOCMAWDOCUMENTBASICTEMPLPERSONGROUP1: Person Group"));
      headbar.addCustomCommand("activateApprovalTempl", mgr.translate("DOCMAWDOCUMENTBASICTEMPLAPPROVALTEMPL1: Approval Template"));
      headbar.addCustomCommand("activateAccessTempl", mgr.translate("DOCMAWDOCUMENTBASICTEMPLACCESSTEMPL1: Access Template"));


      //---------------------------------------------------------------------
      //-------------- ITEM DUMMY -------------------------------------------
      //---------------------------------------------------------------------

      itemdummy = mgr.newASPBlock("ITEMDUMMY");

      f = itemdummy.addField("ITEMDUMMY_INFO");
      f.setHidden();

      f = itemdummy.addField("ITEMDUMMY_OBJID");
      f.setHidden();

      f = itemdummy.addField("ITEMDUMMY_OBJVERSION");
      f.setHidden();

      f = itemdummy.addField("ITEMDUMMY_ATTR");
      f.setHidden();

      f = itemdummy.addField("ITEMDUMMY_ACTION");
      f.setHidden();

      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 0 ---------------------------------------
      //---------------------------------------------------------------------

      itemblk0 = mgr.newASPBlock("ITEM0");

      f = itemblk0.addField("ITEM0_INFO");
      f.setHidden();
      f.setFunction("''");

      f = itemblk0.addField("ITEM0_ACTION");
      f.setHidden();
      f.setFunction("''");

      f = itemblk0.addField("ITEM0_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk0.addField("ITEM0_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk0.addField("ITEM0_DOC_CLASS");
      f.setDbName("DOC_CLASS");
      //   f.setAlignment("CENTER");
      f.setSize(12);
      f.setMaxLength(12);
      f.setMandatory();
      f.setReadOnly();
      f.setInsertable();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCUMENTBASICTEMPLITEM0DOCCLASS: Doc Class");

      f = itemblk0.addField("ITEM0_CLASS_NAME");
      f.setDbName("DOC_NAME");
      //   f.setAlignment(f.ALIGN_CENTER);
      f.setSize(24);
      f.setMaxLength(24);
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICTEMPLITEM0CLASSNAME: Description");

      itemblk0.addField("DOC_CONTROL").
      setSize(5).
      setCheckBox("FALSE,TRUE").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM0DOCCONTROL: Doc Control").
      setInsertable();
      
      itemblk0.addField("NEW_CONTROL").
      setSize(5).
      setCheckBox("FALSE,TRUE").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM0NEWCONTROL: New Control").
      setInsertable();
      
      itemblk0.addField("REV_CONTROL").
      setSize(5).
      setCheckBox("FALSE,TRUE").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM0REVCONTROL: Revision Control").
      setInsertable();
      
      itemblk0.addField("COMP_DOC").
      setSize(5).
      setCheckBox("FALSE,TRUE").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM0COMPDOC: Company Doc").
      setInsertable();
      
      itemblk0.addField("SUPP_DOC").
      setSize(5).
      setCheckBox("FALSE,TRUE").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM0SUPPDOC: Exchange Doc").
      setInsertable();
      
      itemblk0.addField("TEMP_DOC").
      setSize(5).
      setCheckBox("FALSE,TRUE").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM0TEMPDOC: Temporary Doc").
      setInsertable();
      
      itemblk0.addField("CLASS_ORDER", "Number").
      setSize(5).
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM0CLASSORDER: Class Order").
      setInsertable();
      
      itemblk0.addField("PAGE_URL").
      setSize(50).
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM0PAGEURL: Page Url").
      setInsertable();
      
      itemblk0.setView("DOC_CLASS");
      itemblk0.defineCommand("DOC_CLASS_API","New__,Modify__,Remove__");

      itemset0 = itemblk0.getASPRowSet();

      itembar0 = mgr.newASPCommandBar(itemblk0);
      itembar0.defineCommand(itembar0.OKFIND,"okFindITEM0");
      itembar0.defineCommand(itembar0.NEWROW,"newRowITEM0");
      itembar0.defineCommand(itembar0.COUNTFIND,"countFindITEM0");
      itembar0.defineCommand(itembar0.SAVERETURN,"saveReturn0");
      itembar0.disableCommand(itembar0.DUPLICATEROW);

      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("DOCMAWDOCUMENTBASICTEMPLITEM0DOCCLASSTITLE: Document Classes"));

      itemlay0 = itemblk0.getASPBlockLayout();
      //   itemlay0.setFieldSpan("ITEM0_DOC_CLASS",12,30);
      //   itemlay0.setFieldSpan("ITEM0_CLASS_NAME",12,30);
      //   itemlay0.setDataSpan("ITEM0_DOC_CLASS",30);
      //   itemlay0.setDataSpan("ITEM0_CLASS_NAME",30);
      //   itemlay0.setLabelSpan("ITEM0_DOC_CLASS",5);
      //   itemlay0.setLabelSpan("ITEM0_CLASS_NAME",5);
      // itemlay0.setColumnWidth(1,12,30);
      // itemlay0.setColumnWidth(2,12,30);
      // itemlay0.setSpaceBetween(2);
      // itemlay0.setSpaceAfter(10);

      itemlay0.setDialogColumns(2);
      itemlay0.setDefaultLayoutMode(itemlay0.MULTIROW_LAYOUT);

      //
      // Sub Class
      //
      
      doc_sub_class_blk = mgr.newASPBlock("SUBCLASS");
      doc_sub_class_blk.addField("SUBCLASS_OBJID").
      setHidden().
      setDbName("OBJID");

      doc_sub_class_blk.addField("SUBCLASS_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      doc_sub_class_blk.addField("SUBCLASS_DOC_CLASS").
      setDbName("DOC_CLASS").
      setMandatory().
      setInsertable().
      setHidden().
      setLabel("DOCSUBCLASSSUBCLASSDOCCLASS: Doc Class").
      setSize(10);

      doc_sub_class_blk.addField("SUB_CLASS").
      setMandatory().
      setInsertable().
      setLabel("DOCSUBCLASSSUBCLASS: Sub Class").
      setSize(20);

      doc_sub_class_blk.addField("SUB_CLASS_NAME").
      setInsertable().
      setLabel("DOCSUBCLASSSUBCLASSNAME: Sub Class Name").
      setSize(20);
      
      doc_sub_class_blk.addField("SUB_PAGE_URL").
      setDbName("PAGE_URL").
      setSize(50).
      setLabel("DOCSUBCLASSSUBPAGEURL: Page Url").
      setInsertable();

      doc_sub_class_blk.addField("NOTE").
      setInsertable().
      setLabel("DOCSUBCLASSNOTE: Note").
      setSize(50);

      doc_sub_class_blk.setView("DOC_SUB_CLASS");
      doc_sub_class_blk.defineCommand("DOC_SUB_CLASS_API","New__,Modify__,Remove__");
      doc_sub_class_blk.setMasterBlock(itemblk0);
      doc_sub_class_set = doc_sub_class_blk.getASPRowSet();
      doc_sub_class_bar = mgr.newASPCommandBar(doc_sub_class_blk);
      doc_sub_class_bar.defineCommand(doc_sub_class_bar.OKFIND, "okFindITEMSUB");
      doc_sub_class_bar.defineCommand(doc_sub_class_bar.NEWROW, "newRowITEMSUB");
      doc_sub_class_tbl = mgr.newASPTable(doc_sub_class_blk);
      doc_sub_class_tbl.setTitle("DOCSUBCLASSITEMHEAD1: DocSubClass");
      doc_sub_class_tbl.enableRowSelect();
      doc_sub_class_tbl.setWrap();
      doc_sub_class_lay = doc_sub_class_blk.getASPBlockLayout();
      doc_sub_class_lay.setDefaultLayoutMode(doc_sub_class_lay.MULTIROW_LAYOUT);

      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 1 ---------------------------------------
      //---------------------------------------------------------------------

      itemblk1 = mgr.newASPBlock("ITEM1");

      f = itemblk1.addField("ITEM1_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk1.addField("ITEM1_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk1.addField("ITEM1_GROUP_ID");
      f.setDbName("GROUP_ID");
      f.setSize(20);
      f.setMaxLength(20);
      f.setMandatory();
      f.setReadOnly();
      f.setUpperCase();
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICTEMPLITEM1GROUPID: Group ID");

      f = itemblk1.addField("ITEM1_GROUP_DESCRIPTION");
      f.setDbName("GROUP_DESCRIPTION");
      f.setSize(30);
      f.setMaxLength(2000);
      f.setMandatory();
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICTEMPLITEM1GROUPDESCRIPTION: Group Description");

      itemblk1.setView("DOCUMENT_GROUP");
      itemblk1.defineCommand("DOCUMENT_GROUP_API","New__,Modify__,Remove__");

      itemset1 = itemblk1.getASPRowSet();

      itembar1 = mgr.newASPCommandBar(itemblk1);
      itembar1.defineCommand(itembar1.OKFIND,"okFindITEM1");
      itembar1.defineCommand(itembar1.NEWROW,"newRowITEM1");
      itembar1.defineCommand(itembar1.CANCELNEW,"cancelNewITEM1");
      itembar1.defineCommand(itembar1.COUNTFIND,"countFindITEM1");
      itembar1.defineCommand(itembar1.SAVENEW,"SaveNewITEM1");
      itembar1.defineCommand(itembar1.SAVERETURN,"SaveReturnITEM1");
      itembar1.disableCommand(itembar1.DUPLICATEROW);
      itembar1.addSecureCustomCommand("getCopyDialog",mgr.translate("DOCMAWDOCUMENTBASICTEMPLCGM: Copy Group Members..."),"DOCUMENT_GROUP_MEMBERS_API.Copy_Members_Of_Group");//Bug Id 70286

      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("DOCMAWDOCUMENTBASICTEMPLITEM1DOCGROUPTITLE: Document Group"));

      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDialogColumns(1);
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);


      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 2 ---------------------------------------
      //---------------------------------------------------------------------

      itemblk2 = mgr.newASPBlock("ITEM2");

      itemblk2.addField("ITEM2_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk2.addField("ITEM2_OBJVERSION").
      setDbName("OBJVERSION").
      setMandatory().
      setHidden();

      itemblk2.addField("ITEM2_GROUP_ID").
      setDbName("GROUP_ID").
      setMandatory().
      setHidden();

      itemblk2.addField("ITEM2_PERSON_ID").
      setDbName("PERSON_ID").
      setSize(20).
      setMaxLength(30).
      setMandatory().
      setReadOnly().
      setInsertable().
      setUpperCase().
      setDynamicLOV("PERSON_INFO_LOV").
      setCustomValidation("ITEM2_PERSON_ID", "ITEM2_NAME,ITEM2_USER_ID,ITEM2_PERSON_DEPT,ITEM2_PERSON_DEPT_NAME").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM2PERSONID: Person ID");

      itemblk2.addField("ITEM2_NAME").
      setDbName("NAME").
      setSize(20).
      setReadOnly().
      setFunction("PERSON_INFO_API.Get_Name(PERSON_ID)").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM2NAME: Name");

      itemblk2.addField("ITEM2_PERSON_DEPT").
      setSize(20).
      setReadOnly().
      setFunction("GENERAL_ORG_POS_PERSON_API.Get_Person_Def_Dept(:ITEM2_PERSON_ID)").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM2PERSONDEPT: Person Dept");
      
      itemblk2.addField("ITEM2_PERSON_DEPT_NAME").
      setSize(20).
      setReadOnly().
      setFunction("GENERAL_ORG_POS_PERSON_API.Get_Person_Def_Dept_Desc(:ITEM2_PERSON_ID)").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM2PERSONDEPTNAME: Person Dept Name");
      
      itemblk2.addField("ITEM2_USER_ID").
      setDbName("USER_ID").
      setSize(20).
      setReadOnly().
      setUpperCase().
      setFunction("PERSON_INFO_API.Get_User_Id(PERSON_ID)").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM2USERID: User ID");

      itemblk2.addField("ITEM2_NOTE").
      setDbName("NOTE").
      setSize(30).
      setMaxLength(2000).
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM2NOTE: Note");

      itemblk2.setView("DOCUMENT_GROUP_MEMBERS");
      itemblk2.defineCommand("DOCUMENT_GROUP_MEMBERS_API","New__,Modify__,Remove__");
      itemblk2.setMasterBlock(itemblk1);

      itemset2 = itemblk2.getASPRowSet();

      itembar2 = mgr.newASPCommandBar(itemblk2);
      itembar2.defineCommand(itembar2.OKFIND,"okFindITEM2");
      itembar2.defineCommand(itembar2.NEWROW,"newRowITEM2");
      itembar2.defineCommand(itembar2.COUNTFIND,"countFindITEM2");
      itembar2.disableCommand(itembar2.DUPLICATEROW);

      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("DOCMAWDOCUMENTBASICTEMPLITEM2DOCGROUPMEMBERTITLE: Document Group Memebers"));

      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDefaultLayoutMode(itemlay2.MULTIROW_LAYOUT);

      itemlay2.setSimple("ITEM2_NAME");
      itemlay2.setSimple("ITEM2_PERSON_DEPT_NAME");

      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 3 ---------------------------------------
      //---------------------------------------------------------------------

      itemblk3 = mgr.newASPBlock("ITEM3");

      itemblk3.addField("ITEM3_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk3.addField("ITEM3_OBJVERSION").
      setDbName("OBJVERSION").
      setMandatory().
      setHidden();

      f = itemblk3.addField("ITEM3_PROFILE_ID");
      f.setDbName("PROFILE_ID");
      f.setSize(16);
      f.setMaxLength(10);
      f.setMandatory();
      f.setInsertable();
      f.setReadOnly();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCUMENTBASICTEMPLITEM3PROFILEID: Approval Template");

      f = itemblk3.addField("ITEM3_DESCRIPTION");
      f.setDbName("DESCRIPTION");
      f.setSize(40);
      f.setMaxLength(40);
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTBASICTEMPLITEM3DESCRIPTION: Profile Description");

      itemblk3.setView("APPROVAL_PROFILE");
      itemblk3.defineCommand("APPROVAL_PROFILE_API","New__,Modify__,Remove__");

      itemset3 = itemblk3.getASPRowSet();

      itembar3 = mgr.newASPCommandBar(itemblk3);
      itembar3.defineCommand(itembar3.OKFIND,"okFindITEM3");
      itembar3.defineCommand(itembar3.COUNTFIND,"countFindITEM3");
      itembar3.defineCommand(itembar3.NEWROW,"newRowITEM3");
      itembar3.defineCommand(itembar3.CANCELNEW,"cancelNewITEM3");
      itembar3.disableCommand(itembar3.DUPLICATEROW);
      itembar3.addSecureCustomCommand("getCopyApprTemplDialog",mgr.translate("DOCMAWDOCUMENTBASICTEMPLCAT: Copy Approval Template..."),"Approval_Template_API.COPY_TEMPLATE__");//Bug Id 70286

      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setTitle(mgr.translate("DOCMAWDOCUMENTBASICTEMPLITEM3DOCAPPTEMPLTITLE: Approval Template"));

      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDialogColumns(1);
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);


      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 4 ---------------------------------------
      //---------------------------------------------------------------------

      itemblk4 = mgr.newASPBlock("ITEM4");

      itemblk4.addField("ITEM4_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk4.addField("ITEM4_OBJVERSION").
      setDbName("OBJVERSION").
      setMandatory().
      setHidden();

      itemblk4.addField("ITEM4_PROFILE_ID").
      setDbName("PROFILE_ID").
      setMandatory().
      setHidden();

      itemblk4.addField("ITEM4_LINE_NO","Number"). 
      setDbName("LINE_NO").
      setMandatory().
      setHidden();

      itemblk4.addField("ITEM4_STEP_NO","Number").
      setDbName("STEP_NO").
      setReadOnly().
      setInsertable().
      setSize(3).
      setMaxLength(3).
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM4STEPNO: Approval Step No");

      itemblk4.addField("ITEM4_DESCRIPTION").
      setDbName("DESCRIPTION").
      setSize(30).
      setMaxLength(40).
      setMandatory().
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM4DESCRIPTION: Description");

      itemblk4.addField("ITEM4_PERSON_ID").
      setDbName("PERSON_ID").
      setReadOnly().
      setInsertable().
      setSize(30).
      setMaxLength(40).
      setDynamicLOV("PERSON_INFO_LOV").
      // setCustomValidation("ITEM4_PERSON_ID", "ITEM4_NAME, ITEM4_GROUP_ID, ITEM4_GROUP_DESCRIPTION").
      setCustomValidation("ITEM4_PERSON_ID", "ITEM4_NAME").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM4PERSONID: Person ID");

      itemblk4.addField("ITEM4_NAME").
      setSize(30).
      setMaxLength(40).
      setReadOnly().
      setFunction("PERSON_INFO_API.Get_Name(:ITEM4_PERSON_ID)").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM4NAME: Name");

      itemblk4.addField("ITEM4_GROUP_ID").
      setDbName("GROUP_ID").
      setReadOnly().
      setInsertable().
      setSize(30).
      setMaxLength(40).
      setDynamicLOV("DOCUMENT_GROUP").
      // setCustomValidation("ITEM4_GROUP_ID", "ITEM4_GROUP_DESCRIPTION, ITEM4_PERSON_ID, ITEM4_NAME").
      setCustomValidation("ITEM4_GROUP_ID", "ITEM4_GROUP_DESCRIPTION").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM4GROUPID: Group ID");

      itemblk4.addField("ITEM4_GROUP_DESCRIPTION").
      setSize(30).
      setMaxLength(40).
      setReadOnly().
      setFunction("DOCUMENT_GROUP_API.Get_Group_Description(:ITEM4_GROUP_ID)").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM4GROUPDESCRIPTION: Description");
       
      itemblk4.addField("ITEM3_SECURITY_CHECKPOINT_REQ").
      setDbName("SECURITY_CHECKPOINT_REQ").
      setCheckBox("FALSE,TRUE").
      setSize(15).
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM4SECURITYCHECK: Security Checkpoint Required");

      itemblk4.setView("APPROVAL_TEMPLATE");
      itemblk4.defineCommand("APPROVAL_TEMPLATE_API","New__,Modify__,Remove__");
      itemblk4.setMasterBlock(itemblk3);

      itemset4 = itemblk4.getASPRowSet();

      itembar4 = mgr.newASPCommandBar(itemblk4);
      itembar4.defineCommand(itembar4.OKFIND,"okFindITEM4");
      itembar4.defineCommand(itembar4.COUNTFIND,"countFindITEM4");
      itembar4.defineCommand(itembar4.NEWROW,"newRowITEM4");
      itembar4.disableCommand(itembar4.DUPLICATEROW);

      itemtbl4 = mgr.newASPTable(itemblk4);
      itemtbl4.setTitle(mgr.translate("DOCMAWDOCUMENTBASICTEMPLITEM4APPTEMPLDETTITLE: Approval Template Detail"));

      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDialogColumns(2);
      itemlay4.setDefaultLayoutMode(itemlay4.MULTIROW_LAYOUT);


      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 5 ---------------------------------------
      //---------------------------------------------------------------------

      itemblk5 = mgr.newASPBlock("ITEM5");

      itemblk5.addField("ITEM5_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk5.addField("ITEM5_OBJVERSION").
      setDbName("OBJVERSION").
      setMandatory().
      setHidden();

      f = itemblk5.addField("ITEM5_DOC_CLASS");
      f.setDbName("DOC_CLASS");
      f.setSize(12);
      f.setReadOnly();
      f.setMandatory();
      f.setUpperCase();
      f.setDynamicLOV("DOC_CLASS"); 
      f.setLabel("DOCMAWDOCUMENTBASICTEMPLITEM5DOCCLASS: Document Class");

      f = itemblk5.addField("ITEM5_DOC_NAME");
      f.setDbName("DOC_NAME");
      f.setReadOnly();
      f.setSize(24);
      f.setMaxLength(24);
      f.setLabel("DOCMAWDOCUMENTBASICTEMPLITEM5DOCNAME: Description");

      itemblk5.setView("DOC_CLASS");
      itemblk5.defineCommand("DOC_CLASS_API","New__,Modify__,Remove__");

      itemset5 = itemblk5.getASPRowSet();

      itembar5 = mgr.newASPCommandBar(itemblk5);
      itembar5.defineCommand(itembar5.OKFIND,"okFindITEM5");
      itembar5.defineCommand(itembar5.COUNTFIND,"countFindITEM5");
      itembar5.defineCommand(itembar5.NEWROW,"newRowITEM5");
      itembar5.defineCommand(itembar5.CANCELNEW,"cancelNewITEM5");
      itembar5.defineCommand (itembar5.CANCELFIND,"cancelFindITEM5");
      itembar5.disableCommand(itembar5.DUPLICATEROW);
      itembar5.disableCommand(itembar5.EDITROW);
      itembar5.disableCommand(itembar5.DELETE);
      itembar5.disableCommand(itembar5.NEWROW);

      itemtbl5 = mgr.newASPTable(itemblk5);
      itemtbl5.setTitle(mgr.translate("DOCMAWDOCUMENTBASICTEMPLITEM5ACCESSTEMPLTITLE: Access Template"));

      itemlay5 = itemblk5.getASPBlockLayout();
      itemlay5.setDialogColumns(1);
      itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);


      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 6 ---------------------------------------
      //---------------------------------------------------------------------

      itemblk6 = mgr.newASPBlock("ITEM6");

      itemblk6.addField("ITEM6_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk6.addField("ITEM6_OBJVERSION").
      setDbName("OBJVERSION").
      setMandatory().
      setHidden();

      itemblk6.addField("ITEM6_DOC_CLASS").
      setDbName("DOC_CLASS").
      setMandatory().
      setHidden();

      itemblk6.addField("ITEM6_LINE_NO").
      setDbName("LINE_NO").
      setMandatory().
      setHidden();

      itemblk6.addField("ITEM6_GROUP_ID").
      setDbName("GROUP_ID").
      setSize(20).
      setMaxLength(20).
      setDynamicLOV("DOCUMENT_GROUP").
      //            setCustomValidation("ITEM6_GROUP_ID", "ITEM6_GROUP_DESCRIPTION, ITEM6_PERSON_ID, ITEM6_NAME, ITEM6_USER_ID").
      //    Modified to disable automatic clearing of ITEM6_PERSON_ID, ITEM6_NAME, and ITEM6_USER_ID
      setCustomValidation("ITEM6_GROUP_ID", "ITEM6_GROUP_DESCRIPTION").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM6GROUPID: Group ID");

      itemblk6.addField("ITEM6_GROUP_DESCRIPTION").
      setSize(30).
      setMaxLength(40).
      setReadOnly().
      setFunction("DOCUMENT_GROUP_API.Get_Group_Description(:ITEM6_GROUP_ID)").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM6GROUPDESCRIPTION: Group Description");

      itemblk6.addField("ITEM6_PERSON_ID").
      setDbName("PERSON_ID").
      setSize(20).
      setMaxLength(30).
      setDynamicLOV("PERSON_INFO_LOV").
      //            setCustomValidation("ITEM6_PERSON_ID", "ITEM6_NAME, ITEM6_USER_ID, ITEM6_GROUP_ID, ITEM6_GROUP_DESCRIPTION").
      //    Modified to disable automatic clearing of ITEM6_GROUP_ID, and ITEM6_GROUP_DESCRIPTION
      setCustomValidation("ITEM6_PERSON_ID", "ITEM6_NAME, ITEM6_USER_ID").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM6PERSONID: Person ID");

      itemblk6.addField("ITEM6_NAME").
      setSize(30).
      setMaxLength(40).
      setReadOnly().
      setFunction("PERSON_INFO_API.Get_Name(:ITEM6_PERSON_ID)").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM6NAME: Person Name");

      itemblk6.addField("ITEM6_USER_ID").
      setSize(20).
      setMaxLength(30).
      setReadOnly().
      setFunction("PERSON_INFO_API.Get_User_Id(:ITEM6_PERSON_ID)").
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM6USERID: User ID");

      //DMPR303 START
      itemblk6.addField("ITEM6_ADMIN_ACCESS").
      setDbName("ADMIN_ACCESS").
      setCheckBox("0,1").
      setSize(20).
      setMaxLength(20).
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM6ADMINACCESS: Admin Access");    
      //DMPR303 END.

      itemblk6.addField("ITEM6_EDIT_ACCESS").
      setDbName("EDIT_ACCESS").
      setCheckBox("0,1").
      setSize(20).
      setMaxLength(20).
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM6EDITACCESS: Edit Access");

      itemblk6.addField("ITEM6_VIEW_ACCESS").
      setDbName("VIEW_ACCESS").
      setCheckBox("0,1").
      setSize(20).
      setMaxLength(20).
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM6VIEWACCESS: View Access");
      
      itemblk6.addField("ITEM6_DOWNLOAD_ACCESS").
      setDbName("DOWNLOAD_ACCESS").
      setCheckBox("0,1").
      setSize(20).
      setMaxLength(20).
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM6DOWNLOADACCESS: Download Access");

      itemblk6.addField("ITEM6_PRINT_ACCESS").  
      setDbName("PRINT_ACCESS").        
      setCheckBox("0,1").
      setSize(20).
      setMaxLength(20).
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM6PRINTACCESS: Print Access");
                      
      itemblk6.addField("ITEM6_NOTE").
      setDbName("NOTE").
      setSize(30).
      setMaxLength(2000).
      setLabel("DOCMAWDOCUMENTBASICTEMPLITEM6NOTE: Note");

      itemblk6.setView("DOCUMENT_ACCESS_TEMPLATE");
      itemblk6.defineCommand("DOCUMENT_ACCESS_TEMPLATE_API","New__,Modify__,Remove__");
      itemblk6.setMasterBlock(itemblk5);

      itemset6 = itemblk6.getASPRowSet();

      itembar6 = mgr.newASPCommandBar(itemblk6);
      itembar6.defineCommand(itembar6.OKFIND,"okFindITEM6");
      itembar6.defineCommand(itembar6.COUNTFIND,"countFindITEM6");
      itembar6.defineCommand(itembar6.NEWROW,"newRowITEM6");
      itembar6.disableCommand(itembar6.DUPLICATEROW);

      itemtbl6 = mgr.newASPTable(itemblk6);
      itemtbl6.setTitle(mgr.translate("DOCMAWDOCUMENTBASICTEMPLITEM6ACCESSTEMPLDETTITLE: Access Template Detail"));

      itemlay6 = itemblk6.getASPBlockLayout();
      itemlay6.setDialogColumns(2);
      itemlay6.setDefaultLayoutMode(itemlay6.MULTIROW_LAYOUT);


      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 7 ---------------------------------------
      //---------------------------------------------------------------------
      //----------------- Copy Group Members Dialog -------------------------

      itemblk7 = mgr.newASPBlock("ITEM7");

      itemblk7.addField("ITEM7_GROUP_ID").
      setSize(20).
      setMaxLength(20).
      setDbName("GROUP_ID").
      setReadOnly().
      setLabel("Group ID").
      setUpperCase().
      setDynamicLOV("DOCUMENT_GROUP").
      setCustomValidation("ITEM7_GROUP_ID", "ITEM7_GROUP_DESCRIPTION");

      itemblk7.addField("ITEM7_GROUP_DESCRIPTION").
      setSize(30).
      setMaxLength(100).
      setReadOnly().
      setDbName("GROUP_DESCRIPTION");

      itemblk7.setView("DOCUMENT_GROUP");
      itemblk7.setTitle (mgr.translate("DOCMAWDOCUMENTBASICTEMPLITEM7SELGRPCPFROM: Select Group to Copy From"));

      itemset7 = itemblk7.getASPRowSet();

      itembar7 = mgr.newASPCommandBar(itemblk7);
      itembar7.disableCommand(itembar7.FIND);
      itembar7.disableMinimize();

      itemtbl7 = mgr.newASPTable(itemblk7);

      itemlay7 = itemblk7.getASPBlockLayout();
      itemlay7.setDefaultLayoutMode(itemlay7.CUSTOM_LAYOUT);
      itemlay7.setEditable();



      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 8 ---------------------------------------
      //---------------------------------------------------------------------
      //-------------------- Copy Approval Template Dialog ------------------

      itemblk8 = mgr.newASPBlock("ITEM8");

      itemblk8.addField("ITEM8_PROFILE_ID").
      setSize(10).
      setMaxLength(10).
      setDbName("PROFILE_ID").
      setReadOnly().
      setUpperCase().
      setLabel("Profile ID").
      setDynamicLOV("APPROVAL_PROFILE").
      setCustomValidation("ITEM8_PROFILE_ID", "ITEM8_DESCRIPTION");

      itemblk8.addField("ITEM8_DESCRIPTION").
      setSize(40).
      setMaxLength(40).
      setReadOnly().
      setDbName("DESCRIPTION");

      itemblk8.setView("APPROVAL_PROFILE");
      itemblk8.setTitle (mgr.translate("DOCMAWDOCUMENTBASICTEMPLITEM8SELPROFCPFROM: Select Profile to Copy From"));

      itemset8 = itemblk8.getASPRowSet();

      itembar8 = mgr.newASPCommandBar(itemblk8);
      itembar8.disableCommand(itembar8.FIND);
      itembar8.disableMinimize();

      itemtbl8 = mgr.newASPTable(itemblk8);

      itemlay8 = itemblk8.getASPBlockLayout();
      itemlay8.setDefaultLayoutMode(itemlay8.CUSTOM_LAYOUT);
      //itemlay8.setDefaultLayoutMode(itemlay8.SINGLE_LAYOUT);
      // itemlay8.setEditable();



      //---------------------------------------------------------------------
      //-------------- DEFINITIONS OF TABS ----------------------------------
      //---------------------------------------------------------------------

      tabs = mgr.newASPTabContainer();
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTBASICTEMPLDOCCLASSES2: Document Classes"), "javascript:commandSet('HEAD.activateDocClasses','')");
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTBASICTEMPLPERSONGROUP2: Person Group"),"javascript:commandSet('HEAD.activateDocGroup','')");
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTBASICTEMPLAPPROVALTEMPL2: Approval Template"),"javascript:commandSet('HEAD.activateApprovalTempl','')");
      tabs.addTab(mgr.translate("DOCMAWDOCUMENTBASICTEMPLACCESSTEMPL2: Access Template"),"javascript:commandSet('HEAD.activateAccessTempl','')");

      tabs.setContainerWidth(700);
      tabs.setLeftTabSpace(1);
      tabs.setContainerSpace(5);
      tabs.setTabWidth(100);
   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCUMENTBASICTEMPLTITLE: Basic Data - Classes, Groups, Templates";
   }

   protected String getTitle()
   {
      return "DOCMAWDOCUMENTBASICTEMPLTITLE: Basic Data - Classes, Groups, Templates";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      int row7Count=itemset7.countRows();
      int row8Count=itemset8.countRows();
      eval(itemblk7.generateAssignments());
      eval(itemblk8.generateAssignments());
      String commandbar_space ="<td widh=10>&nbsp;</td>";
      appendToHTML(tabs.showTabsInit());
      if (showDialog && tabs.getActiveTab()==2)
      {

         String our_buttons = fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWDOCUMENTGROUPCANCEL:   Cancel  "),"");
         our_buttons += fmt.drawSubmit("OK",mgr.translate("DOCMAWDOCUMENTGROUPOK:     OK     "),"");

         appendToHTML("      <table id=\"topTable\" width=\"500\" border=0 cellpadding=0 cellspacing=0>\n");
         appendToHTML("<tr ><td colspan=\"3\"><!-- bar start -->"+itembar7.showBar()+"<!-- bar end --></td></tr>");//first row has only one column which contains command bar
         appendToHTML("<tr >");// second row starts: it has 3 columns >> two sapaces and our lay out.
         appendToHTML(commandbar_space);//space 1(row :2 col:1)
         appendToHTML("<td >");//our lay out is in this column (row :2 col:2 starts)
         appendToHTML("   <!-- ********* our layout starts-->      <table id=\"cntITEM7\" border=0 cellspacing=0 cellpadding=2 class=\"BlockLayoutTable\" width=\"100%\"");
         appendToHTML("            <tr>\n");//row: 1
         appendToHTML("               <td width=10>&nbsp;</td>\n");
         appendToHTML("               <td width=210>");
         appendToHTML(fmt.drawReadLabel(mgr.translate("DOCMAWDOCUMENTBASICTEMPLGRPID: Group ID:")));
         appendToHTML("</td>\n");
         appendToHTML("            </tr>\n");

         appendToHTML("            <tr>\n");//row:2
         appendToHTML("               <td>&nbsp;</td>\n");
         appendToHTML("               <td>");

         itemset7.addRow(mgr.newASPBuffer());//just to take the tag
         String html_tag = mgr.getASPField("ITEM7_GROUP_ID").getTag();
         appendToHTML(fmt.drawTextField("ITEM7_GROUP_ID","","onchange=\"validateItem7GroupId(-1)\""+mgr.getASPField("ITEM7_GROUP_ID").getTag()));
         itemset7.clear();
         appendToHTML("</td>\n");
         appendToHTML("            </tr>\n");

         appendToHTML("            <tr>\n");//row:3
         appendToHTML("               <td>&nbsp;</td>\n");
         appendToHTML("               <td>");
         appendToHTML(fmt.drawReadLabel(mgr.translate("DOCMAWDOCUMENTBASICTEMPLGRPDES: Group Description:")));
         appendToHTML("</td>\n");
         appendToHTML("            </tr>\n");

         appendToHTML("            <tr>\n");//row:4
         appendToHTML("               <td>&nbsp;</td>\n");
         appendToHTML("               <td >");
         appendToHTML(fmt.drawReadOnlyTextField("ITEM7_GROUP_DESCRIPTION","",mgr.getASPField("ITEM7_GROUP_DESCRIPTION").getTag()));
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


      }
      else if (showTmplDialog && tabs.getActiveTab()==3)
      {
         String our_buttons = fmt.drawSubmit("CANCEL2",mgr.translate("DOCMAWDOCUMENTBASICTEMPLCANCEL:   Cancel  "),"");
         our_buttons += fmt.drawSubmit("OK2",mgr.translate("DOCMAWDOCUMENTBASICTEMPLOK:     OK     "),"");

         appendToHTML("      <table id=\"topTable\" width=\"500\" border=0 cellpadding=0 cellspacing=0>\n");
         appendToHTML("<tr ><td colspan=\"3\"><!-- bar start -->"+itembar8.showBar()+"<!-- bar end --></td></tr>");//first row has only one column which contains command bar
         appendToHTML("<tr >");// second row starts: it has 3 columns >> two sapaces and our lay out.
         appendToHTML(commandbar_space);//space 1(row :2 col:1)
         appendToHTML("<td >");//our lay out is in this column (row :2 col:2 starts)
         appendToHTML("   <!-- ********* our layout starts-->      <table id=\"cntITEM8\" border=0 cellspacing=0 cellpadding=2 class=\"BlockLayoutTable\" width=\"100%\"");
         appendToHTML("            <tr>\n");//row: 1
         appendToHTML("               <td width=10>&nbsp;</td>\n");
         appendToHTML("               <td width=210>");
         appendToHTML(fmt.drawReadLabel("DOCMAWDOCUMENTBASICTEMPLPRPOFID: Template:"));
         appendToHTML("</td>\n");
         appendToHTML("            </tr>\n");

         appendToHTML("            <tr>\n");//row:2
         appendToHTML("               <td>&nbsp;</td>\n");
         appendToHTML("               <td>");

         itemset8.addRow(mgr.newASPBuffer());//just to take the tag
         String html_tag = mgr.getASPField("ITEM8_PROFILE_ID").getTag();
         appendToHTML(fmt.drawTextField("ITEM8_PROFILE_ID","","onchange=\"validateItem8ProfileId(-1)\""+mgr.getASPField("ITEM8_PROFILE_ID").getTag()));
         itemset8.clear();
         appendToHTML("</td>\n");
         appendToHTML("            </tr>\n");

         appendToHTML("            <tr>\n");//row:3
         appendToHTML("               <td>&nbsp;</td>\n");
         appendToHTML("               <td>");
         appendToHTML(fmt.drawReadLabel("DOCMAWDOCUMENTBASICTEMPLPROFDES: Description:"));
         appendToHTML("</td>\n");
         appendToHTML("            </tr>\n");

         appendToHTML("            <tr>\n");//row:4
         appendToHTML("               <td>&nbsp;</td>\n");
         appendToHTML("               <td >");
         appendToHTML(fmt.drawReadOnlyTextField("ITEM8_DESCRIPTION","",mgr.getASPField("ITEM8_DESCRIPTION").getTag(),40,40)); //Bug Id 84299
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

         showTmplDialog = false;

      }
      else if (tabs.getActiveTab()==1)
      {
         appendToHTML(itemlay0.show());
         if (itemset0.countRows() > 0 && (itemlay0.isSingleLayout() || itemlay0.isCustomLayout()))
            appendToHTML(doc_sub_class_lay.show());
      }
      else if (tabs.getActiveTab()==2)
      {
         appendToHTML(itemlay1.show());
         if (itemset1.countRows()>0 && (itemlay1.isSingleLayout() || itemlay1.isCustomLayout()))
            appendToHTML(itemlay2.show());
      }
      else if (tabs.getActiveTab()==3)
      {
         appendToHTML(itemlay3.show());
         if (itemset3.countRows()>0 && (itemlay3.isSingleLayout() || itemlay3.isCustomLayout()))
            appendToHTML(itemlay4.show());
      }
      else if (tabs.getActiveTab()==4)
      {
         appendToHTML(itemlay5.show());
         if (itemset5.countRows()>0 && (itemlay5.isSingleLayout() || itemlay5.isCustomLayout()))
            appendToHTML(itemlay6.show());
      }
      appendToHTML(tabs.showTabsFinish());
      appendDirtyJavaScript("//=============================================================================\n");
      appendDirtyJavaScript("//   CLIENT FUNCTIONS\n");
      appendDirtyJavaScript("//=============================================================================\n");
      appendDirtyJavaScript("function validateItem7GroupId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    setDirty();\n");
      appendDirtyJavaScript("    if( !checkItem7GroupId(i) ) return;\n");
      appendDirtyJavaScript("    if( getValue_('ITEM7_GROUP_ID',i)=='' )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        getField_('ITEM7_GROUP_DESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("        return;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM7_GROUP_ID'\n");
      appendDirtyJavaScript("        + '&ITEM7_GROUP_ID=' + getValue_('ITEM7_GROUP_ID',i)\n");
      appendDirtyJavaScript("        );\n");
      appendDirtyJavaScript("    if( checkStatus_(r,'ITEM7_GROUP_ID',i,'ITEM7_GROUP_ID') )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        assignValue_('ITEM7_GROUP_DESCRIPTION',i,0);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem8ProfileId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("    setDirty();\n");
      appendDirtyJavaScript("    if( !checkItem8ProfileId(i) ) return;\n");
      appendDirtyJavaScript("    if( getValue_('ITEM8_PROFILE_ID',i)=='' )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        getField_('ITEM8_DESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("        return;\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("    r = __connect(\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM8_PROFILE_ID'\n");
      appendDirtyJavaScript("        + '&ITEM8_PROFILE_ID=' + getValue_('ITEM8_PROFILE_ID',i)\n");
      appendDirtyJavaScript("        );\n");
      appendDirtyJavaScript("    if( checkStatus_(r,'ITEM8_PROFILE_ID',i,'ITEM8_PROFILE_ID') )\n");
      appendDirtyJavaScript("    {\n");
      appendDirtyJavaScript("        assignValue_('ITEM8_DESCRIPTION',i,0);\n");
      appendDirtyJavaScript("    }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function validateItem4PersonId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" if( getRowStatus_('ITEM4',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript(" setDirty();\n");
      appendDirtyJavaScript(" if( !checkItem4PersonId(i) ) return;\n");
      appendDirtyJavaScript(" if( getValue_('ITEM4_PERSON_ID',i)=='' )\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    getField_('ITEM4_NAME',i).value = '';\n");
      appendDirtyJavaScript("    return;\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" r = __connect(\n");//tested
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM4_PERSON_ID'\n");
      appendDirtyJavaScript("    + '&ITEM4_PERSON_ID=' + getValue_('ITEM4_PERSON_ID',i)\n");
      appendDirtyJavaScript("    );\n");
      appendDirtyJavaScript(" if( checkStatus_(r,'ITEM4_PERSON_ID',i,'Person ID') )\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    assignValue_('ITEM4_NAME',i,0);\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if (document.form.ITEM4_GROUP_ID.value!='') {\n");
      appendDirtyJavaScript("    alert(\"");
      appendDirtyJavaScript(bothCannotHaveValues);
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript("    document.form.ITEM4_PERSON_ID.value='';\n");
      appendDirtyJavaScript("    document.form.ITEM4_NAME.value='';\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function validateItem4GroupId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" if( getRowStatus_('ITEM4',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript(" setDirty();\n");
      appendDirtyJavaScript(" if( !checkItem4GroupId(i) ) return;\n");
      appendDirtyJavaScript(" if( getValue_('ITEM4_GROUP_ID',i)=='' )\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    getField_('ITEM4_GROUP_DESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("    return;\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" r = __connect(\n"); //tested
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM4_GROUP_ID'\n");
      appendDirtyJavaScript("    + '&ITEM4_GROUP_ID=' + getValue_('ITEM4_GROUP_ID',i)\n");
      appendDirtyJavaScript("    );\n");
      appendDirtyJavaScript(" if( checkStatus_(r,'ITEM4_GROUP_ID',i,'Group ID') )\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    assignValue_('ITEM4_GROUP_DESCRIPTION',i,0);\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if (document.form.ITEM4_PERSON_ID.value!='') {\n");
      appendDirtyJavaScript("    alert(\"");
      appendDirtyJavaScript(bothCannotHaveValues);
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript("    document.form.ITEM4_GROUP_ID.value='';\n");
      appendDirtyJavaScript("    document.form.ITEM4_GROUP_DESCRIPTION.value='';\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem6GroupId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript(" setDirty();\n");
      appendDirtyJavaScript(" if( !checkItem6GroupId(i) ) return;\n");
      appendDirtyJavaScript(" if( getValue_('ITEM6_GROUP_ID',i)=='' )\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    getField_('ITEM6_GROUP_DESCRIPTION',i).value = '';\n");
      appendDirtyJavaScript("    return;\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" r = __connect(\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM6_GROUP_ID'\n");
      appendDirtyJavaScript("    + '&ITEM6_GROUP_ID=' + getValue_('ITEM6_GROUP_ID',i)\n");
      appendDirtyJavaScript("    );\n");
      appendDirtyJavaScript(" if( checkStatus_(r,'ITEM6_GROUP_ID',i,'Group ID') )\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    assignValue_('ITEM6_GROUP_DESCRIPTION',i,0);\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if (document.form.ITEM6_PERSON_ID.value!='') {\n");
      appendDirtyJavaScript("    alert(\"");
      appendDirtyJavaScript(bothCannotHaveValues);
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript("    document.form.ITEM6_GROUP_ID.value='';\n");
      appendDirtyJavaScript("    document.form.ITEM6_GROUP_DESCRIPTION.value='';\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function validateItem6PersonId(i)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript(" if( getRowStatus_('ITEM6',i)=='QueryMode__' ) return;\n");
      appendDirtyJavaScript(" setDirty();\n");
      appendDirtyJavaScript(" if( !checkItem6PersonId(i) ) return;\n");
      appendDirtyJavaScript(" if( getValue_('ITEM6_PERSON_ID',i)=='' )\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    getField_('ITEM6_NAME',i).value = '';\n");
      appendDirtyJavaScript("    getField_('ITEM6_USER_ID',i).value = '';\n");
      appendDirtyJavaScript("    return;\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" r = __connect(\n");
      appendDirtyJavaScript("        '");
      appendDirtyJavaScript(mgr.getURL());
      appendDirtyJavaScript("?VALIDATE=ITEM6_PERSON_ID'\n");
      appendDirtyJavaScript("    + '&ITEM6_PERSON_ID=' + getValue_('ITEM6_PERSON_ID',i)\n");
      appendDirtyJavaScript("    );\n");
      appendDirtyJavaScript(" if( checkStatus_(r,'ITEM6_PERSON_ID',i,'Person ID') )\n");
      appendDirtyJavaScript(" {\n");
      appendDirtyJavaScript("    assignValue_('ITEM6_NAME',i,0);\n");
      appendDirtyJavaScript("    assignValue_('ITEM6_USER_ID',i,1);\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript(" if (document.form.ITEM6_GROUP_ID.value!='') {\n");
      appendDirtyJavaScript("    alert(\"");
      appendDirtyJavaScript(bothCannotHaveValues);
      appendDirtyJavaScript("\");\n");
      appendDirtyJavaScript("    document.form.ITEM6_PERSON_ID.value='';\n");
      appendDirtyJavaScript("    document.form.ITEM6_NAME.value='';\n");
      appendDirtyJavaScript("    document.form.ITEM6_USER_ID.value='';\n");
      appendDirtyJavaScript(" }\n");
      appendDirtyJavaScript("}\n");
   }

}
