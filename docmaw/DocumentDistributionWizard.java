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
*  File        : DocumentDistributionWizard.java
*  Modified    :
*    2002-12-04  Prsalk  Added Document Sheet.
*    2002-12-30  Dikalk  2002-2 SP3 Merge: "Bug 31903 Increased the field length in sender_person ,receiver_person"
*    2003-04-07  Thwilk - Bug Id 36063- Prevented accessing the receivers through server side methods(List_Persons,List_Groups)
*                         and instead the problem was solved by accessing the related views directly from the clie
*    2003-04-23  Thwilk - Bug Id 36063- Increased the buffer size so that all the users can be seen when this operation is performed.
*    2003-08-08  NiSilk - 2003-2 SP4 Merge:"Bug Id 36063"
*    2003-08-14  InoSlk - Call ID 100767: Modified methods cancel(), runQuery() and printContents().
*    2003-08-29  InoSlk - Call ID 101731: Modified doReset() and clone().
*    2005-09-07  CHRALK - Bug 53145, Added step numbers with the window title in wizard.    
*    2005-10-07  Amnalk - Merged Bug Id 53145.
*    2006-01-24  Rucslk - Merged Bug Id 55626.
*    2006-07-25  BAKALK  Bug ID 58216, Fixed Sql Injection.
*    2007-03-01  NIJALK   Bug 58526, Modified printContents().
*    2007-03-13  NIJALK   Bug 64068, Removed mgr.translate() statements, which are used with ASPHTMLFormatter objects.
*    2008-07-18  SHTHLK   Bug Id 75677, Changed the Step3 of the wizard to load the characters first and then to load the users/groups
*    2008-07-18           based on the selected character. For each character database will be access once to get the data.   
*    2008-07-29  SHTHLK   Bug Id 75677, Aligned the characters and the buttons properly
*    2008-07-29  SHTHLK   Bug Id 75677, Modified frame2NextGetData() and populateUserGroups to set the buffersize to 10,000.
*    2009-01-12  AMNALK   Bug Id 78998, Modified preDefine() and printContents().
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.Arrays; //Bug Id 75677


public class DocumentDistributionWizard extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocumentDistributionWizard");


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
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPBuffer buff;
   private ASPCommand cmd;
   private ASPBuffer data;
   private ASPQuery q;

   private boolean populateListBox;
   private boolean activeFrame0;
   private boolean activeFrame1;
   private boolean activeFrame2;
   private boolean activeFrame3;
   private boolean activeFrame4;
   private boolean frame2Populated;
   private boolean bCloseWindow = false;

   private String sDocClassLst;
   private String sDocNoLst;
   private String sDocSheet;
   private String personList;
   private String tmpIdentityList;
   private String redirectFrom;
   private String sDocRevLst;
   private String tmp;
   private String strObj;
   private String ITEM2_IDENTITY_LIST_0;
   private String ITEM2_PERSON_LIST_0;
   private String ITEM2_GROUP_DESC_0;
   private String ITEM2_GROUP_ID_0;

   private int currow;
   private int i;
   private int count1;
   private int count2;

   //===============================================================
   // Construction
   //===============================================================
   public DocumentDistributionWizard(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans = null;
      buff  = null;
      cmd   = null;
      data  = null;
      q     = null;

      populateListBox = false;
      activeFrame0    = false;
      activeFrame1    = false;
      activeFrame2    = false;
      activeFrame3    = false;
      activeFrame4    = false;
      frame2Populated = false;
      bCloseWindow    = false;

      sDocClassLst    = null;
      sDocNoLst       = null;
      sDocSheet       = null;
      personList      = null;
      tmpIdentityList = null;
      redirectFrom    = null;
      sDocRevLst      = null;
      tmp             = null;
      strObj          = null;
      ITEM2_IDENTITY_LIST_0 = null;
      ITEM2_PERSON_LIST_0   = null;
      ITEM2_GROUP_DESC_0    = null;
      ITEM2_GROUP_ID_0      = null;

      i      = 0;
      currow = 0;
      count1 = 0;
      count2 = 0;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      DocumentDistributionWizard page = (DocumentDistributionWizard)(super.clone(obj));

      //Initializing mutable attributes
      page.trans = null;
      page.buff  = null;
      page.cmd   = null;
      page.data  = null;
      page.q     = null;

      page.populateListBox = false;
      page.activeFrame0    = false;
      page.activeFrame1    = false;
      page.activeFrame2    = false;
      page.activeFrame3    = false;
      page.activeFrame4    = false;
      page.frame2Populated = false;
      page.bCloseWindow    = false;

      page.sDocClassLst    = null;
      page.sDocNoLst       = null;
      page.sDocSheet       = null;
      page.personList      = null;
      page.tmpIdentityList = null;
      page.redirectFrom    = null;
      page.sDocRevLst      = null;
      page.tmp             = null;
      page.strObj          = null;
      page.ITEM2_IDENTITY_LIST_0 = null;
      page.ITEM2_PERSON_LIST_0   = null;
      page.ITEM2_GROUP_DESC_0    = null;
      page.ITEM2_GROUP_ID_0      = null;

      page.i      = 0;
      page.currow = 0;
      page.count1 = 0;
      page.count2 = 0;

      //Cloning immutable attributes
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
      page.f = page.getASPField(f.getName());

      return page;
   }

   public void run()
   {
      populateListBox =  false;
      ASPManager mgr = getASPManager();

      ctx = mgr.getASPContext();
      trans = mgr.newASPTransactionBuffer();
      fmt = mgr.newASPHTMLFormatter();
      frm = mgr.getASPForm();
      log = mgr.getASPLog();
      activeFrame0   = ctx.readFlag("ACTIVEFRAME0",true);
      activeFrame1   = ctx.readFlag("ACTIVEFRAME1",false);
      activeFrame2   = ctx.readFlag("ACTIVEFRAME2",false);
      activeFrame3   = ctx.readFlag("ACTIVEFRAME3",false);
      activeFrame4 = ctx.readFlag("ACTIVEFRAME4",false);
      frame2Populated = ctx.readFlag("FRAME2POPULATED",true);
      buff = ctx.readBuffer("BUFF");
      sDocClassLst = ctx.readValue("DOCCLASS","");
      sDocNoLst = ctx.readValue("DOCNO","");
      //prsalk
      sDocSheet = ctx.readValue("DOCSHEET","");
      sDocNoLst = ctx.readValue("DOCREV","");
      personList = ctx.readValue("PERSONLIST","");

      tmpIdentityList = ctx.readValue("TMPIDENTITYLIST","");
      redirectFrom = ctx.readValue("REDIRECTFROM","");

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (mgr.dataTransfered())
         runQuery();
      else if (mgr.buttonPressed("BACK1") || mgr.buttonPressed("BACK2") || mgr.buttonPressed("BACK3") || mgr.buttonPressed("BACK4"))
         previousFrame();
      else if (mgr.buttonPressed("NEXT0") || mgr.buttonPressed("NEXT1") || mgr.buttonPressed("NEXT2") || mgr.buttonPressed("NEXT3"))
         nextFrame();
      else if (mgr.buttonPressed("FINISH"))
         finish();
      else if (mgr.buttonPressed("CANCEL"))
         cancel();
      //Bug Id 75677, Start
      else if( !mgr.isEmpty(mgr.readValue("execute")) )
            execute();
      //Bug Id 75677, End

      adjust();
      ctx.writeFlag("ACTIVEFRAME0",activeFrame0);
      ctx.writeFlag("ACTIVEFRAME1",activeFrame1);
      ctx.writeFlag("ACTIVEFRAME2",activeFrame2);
      ctx.writeFlag("ACTIVEFRAME3",activeFrame3);
      ctx.writeFlag("ACTIVEFRAME4",activeFrame4);
      ctx.writeFlag("FRAME2POPULATED",frame2Populated);
      ctx.writeBuffer("BUFF",buff);
      ctx.writeValue("DOCCLASS",sDocClassLst);
      ctx.writeValue("DOCNO",sDocNoLst);
      ctx.writeValue("DOCSHEET",sDocSheet);
      ctx.writeValue("DOCREV",sDocNoLst);
      ctx.writeValue("PERSONLIST",personList);
      ctx.writeValue("TMPIDENTITYLIST",tmpIdentityList);
      redirectFrom = ctx.findGlobal("__REDIRECT_FROM","");
      ctx.writeValue("REDIRECTFROM",redirectFrom);
   }

   //=============================================================================
   //  Validation
   //=============================================================================

   //Bug Id 75677, Start
   public void  execute()
   {
       ASPManager mgr = getASPManager();

       String val = mgr.readValue("execute");                        

       //ServerFunction 
       if( "populateUserGroups".equals(val) )
       {
	   mgr.responseWrite(populateUserGroups( mgr.readValue("SELECTED_CHAR")) );
       }    
        mgr.endResponse();
   }

   public String  populateUserGroups(String selected_character)
   {
      //Retrive users/groups that starts with the selected character from the database.
      ASPManager mgr = getASPManager();
      ASPBuffer temp_buff,sub_Buff;

      //Check if there is no restriction to 100 rows only
      trans.clear();
      String sql_Select1="SELECT LTRIM(identity), LTRIM(name) FROM DOC_DIST_PERSON WHERE UPPER(SUBSTR(name,0,1)) = ?";
      q = trans.addQuery("LISTPERSON",sql_Select1);
      q.addParameter("DUMMY",selected_character);
      q.setBufferSize(10000);//Make sure we don't run out of buffer size
      
      String sql_Select2="SELECT LTRIM(group_id),LTRIM(group_description) FROM DOCUMENT_GROUP WHERE UPPER(SUBSTR(group_description,0,1)) = ?";
      q = trans.addQuery("LISTGROUP",sql_Select2);
      q.addParameter("DUMMY",selected_character);
      q.setBufferSize(10000);//Make sure we don't run out of buffer size
      trans = mgr.perform(trans);

      if (trans.getBufferAt(1).countItems() > 0) {
	  //get the persons sub buffer
	  sub_Buff = trans.getBufferAt(1);
	  //string concatenation of zeroth index values-for persons
	  temp_buff=sub_Buff.getBufferAt(0);
	  ITEM2_PERSON_LIST_0= temp_buff.getValueAt(1)+" <"+temp_buff.getValueAt(0)+">"+(char)31; //Bug Id 78998
	  ITEM2_IDENTITY_LIST_0= temp_buff.getValueAt(0)+(char)31;
    
	  //string concatenation of the rest-for persons
	  for (int x=1; x<sub_Buff.countItems()-1; x++)
	  {
	     temp_buff=sub_Buff.getBufferAt(x);
	     ITEM2_PERSON_LIST_0=ITEM2_PERSON_LIST_0+temp_buff.getValueAt(1)+" <"+temp_buff.getValueAt(0)+">"+(char)31; //Bug Id 78998
	     ITEM2_IDENTITY_LIST_0=ITEM2_IDENTITY_LIST_0+temp_buff.getValueAt(0)+(char)31;
	  }
      }
       if (trans.getBufferAt(2).countItems() > 0) {
	  ITEM2_GROUP_DESC_0 = "";
	  ITEM2_GROUP_ID_0 = "";
	   //get the groups sub buffer
	  sub_Buff = trans.getBufferAt(2);
	  //string concatenation of zeroth index values-for groups
	  temp_buff=sub_Buff.getBufferAt(0);
	  ITEM2_GROUP_DESC_0= temp_buff.getValueAt(1)+" <"+temp_buff.getValueAt(0)+">"+(char)31; //Bug Id 78998
	  ITEM2_GROUP_ID_0= temp_buff.getValueAt(0)+(char)31;
    
	  //string concatenation of the rest-for groups
	  for (int x=1; x<sub_Buff.countItems()-1; x++)
	  {
	     temp_buff=sub_Buff.getBufferAt(x);
	     ITEM2_GROUP_DESC_0=ITEM2_GROUP_DESC_0+temp_buff.getValueAt(1)+" <"+temp_buff.getValueAt(0)+">"+(char)31; //Bug Id 78998
	     ITEM2_GROUP_ID_0=ITEM2_GROUP_ID_0+temp_buff.getValueAt(0)+(char)31;
	  }
      }
      return ITEM2_PERSON_LIST_0 +(char)30 +ITEM2_IDENTITY_LIST_0 +(char)29 +ITEM2_GROUP_DESC_0 + (char)30 +ITEM2_GROUP_ID_0;
   }
   //Bug Id 75677, End

   public void  adjust()
   {

      itembar3.enableCommand(itembar3.FORWARD);
      itembar3.enableCommand(itembar3.BACKWARD);
      if (itemlay3.getLayoutMode() == itemlay3.EDIT_LAYOUT)
      {
         itembar3.disableCommand(itembar3.FORWARD);
         itembar3.disableCommand(itembar3.BACKWARD);
      }
   }


   public void  runQuery()
   {
      ASPManager mgr = getASPManager();

      ASPBuffer transferred_data = mgr.getTransferedData();
      transferred_data.traceBuffer("TRANSFERRED DATA");

      buff = transferred_data.getBufferAt(1);

      sDocClassLst = "";
      sDocNoLst = "";
      sDocSheet = "";
      sDocRevLst = "";

      ASPBuffer buff1;

      for (i=0;i<buff.countItems();i++)
      {
         buff1 = buff.getBufferAt(i);
         sDocClassLst += buff1.getValueAt(0)+"^";
         sDocNoLst += buff1.getValueAt(1)+"^";
         sDocSheet += buff1.getValueAt(2)+"^";
         sDocRevLst += buff1.getValueAt(3)+"^";
      }

      trans.clear();
      cmd = trans.addCustomCommand("ITD","DOC_DIST_ENGINE_API.Insert_Temp_Documents");
      cmd.addParameter("HEAD_TEMP_SEQ");
      cmd.addParameter("HEAD_DOC_CLASS",sDocClassLst);
      cmd.addParameter("HEAD_DOC_NO",sDocNoLst);
      //prsalk
      cmd.addParameter("HEAD_DOC_SHEET",sDocSheet);
      cmd.addParameter("HEAD_DOC_REV",sDocRevLst);
      trans = mgr.perform(trans);
      data  = trans.getBuffer("ITD/DATA");
      headset.addRow(data);

      trans.clear();
      cmd = trans.addCustomCommand("DDEA","DOC_DIST_ENGINE_API.Get_Dist_Default_Values");
      cmd.addParameter("ITEM0_NO_OF_COPIES");
      cmd.addParameter("ITEM0_DIST_NOTE");
      cmd.addParameter("ITEM0_TRIG_COND");
      cmd.addParameter("ITEM0_TYPE");
      cmd.addParameter("ITEM0_PRIORITY");
      cmd.addParameter("ITEM0_VALID_FROM");
      cmd.addParameter("ITEM0_VALID_TO");
      trans = mgr.perform(trans);
      data   = trans.getBuffer("DDEA/DATA");
      itemset0.addRow(data);
   }


   public void  previousFrame()
   {
      if (activeFrame1)
      {
         // if "Document(s) to Distribute"
         activeFrame0 = true;             //show "General Distribution Settings"
         activeFrame1 = false;
         activeFrame2 = false;
         activeFrame3 = false;
         activeFrame4 = false;
         frame0PreviousGetData();
      }
      else if (activeFrame2)
      {
         // if "Select Receiver(s)"
         activeFrame0 = false;
         activeFrame1 = true;             //show "Document(s) to Distribute"
         activeFrame2 = false;
         activeFrame3 = false;
         activeFrame4 = false;
         frame1PreviousGetData();
      }
      else if (activeFrame3)
      {
         // if "Select Distribution Method(s)"
         activeFrame0 = false;
         activeFrame1 = false;
         activeFrame2 = true;             //show "Select Receiver(s)"
         activeFrame3 = false;
         activeFrame4 = false;
         frame2PreviousGetData();
      }
      else if (activeFrame4)
      {
         // if "Final Settings"
         activeFrame0 = false;
         activeFrame1 = false;
         activeFrame2 = false;
         activeFrame3 = true;             //show "Select Distribution Method(s)"
         activeFrame4 = false;
         frame3PreviousGetData();
      }
   }


   public void  nextFrame()
   {
      //debug("## activeFrame0"+activeFrame0+" activeFrame1 "+activeFrame1+" activeFrame2 "+activeFrame2+" activeFrame3 "+activeFrame3+" activeFrame4 "+activeFrame4);
      if (activeFrame0)
      {
         // if "General Distribution Settings"
         activeFrame0 = false;
         activeFrame1 = true;             //show "Document(s) to Distribute"
         activeFrame2 = false;
         activeFrame3 = false;
         activeFrame4 = false;

         frame1NextGetData();
      }
      else if (activeFrame1)
      {
         // if "Document(s) to Distribute"
         activeFrame0 = false;
         activeFrame1 = false;
         activeFrame2 = true;             //show "Select Receiver(s)"
         activeFrame3 = false;
         activeFrame4 = false;

         frame2NextGetData();
      }
      else if (activeFrame2)
      {
         // if "Select Receiver(s)"
         activeFrame0 = false;
         activeFrame1 = false;
         activeFrame2 = false;
         activeFrame3 = true;             //show "Select Distribution Method(s)"
         activeFrame4 = false;

         frame3NextGetData();
      }
      else if (activeFrame3)
      {
         // if "Select Distribution Method(s)"
         activeFrame0 = false;
         activeFrame1 = false;
         activeFrame2 = false;
         activeFrame3 = false;
         activeFrame4 = true;             //show "Final Settings"

         frame4NextGetData();
      }
   }


   public void  frame1NextGetData()
   {
      ASPManager mgr = getASPManager();

      itemset0.changeRow();
      trans.clear();
      cmd = trans.addCustomCommand("IGDS","DOC_DIST_ENGINE_API.Insert_General_Dist_Settings");
      cmd.addParameter("HEAD_TEMP_SEQ",headset.getValue("HEAD_TEMP_SEQ"));
      cmd.addParameter("ITEM0_NO_OF_COPIES",itemset0.getValue("ITEM0_NO_OF_COPIES"));
      cmd.addParameter("ITEM0_DIST_NOTE",itemset0.getValue("ITEM0_DIST_NOTE"));
      cmd.addParameter("ITEM0_TRIG_COND",itemset0.getValue("ITEM0_TRIG_COND"));
      cmd.addParameter("ITEM0_TYPE",itemset0.getValue("ITEM0_TYPE"));
      cmd.addParameter("ITEM0_PRIORITY",itemset0.getValue("ITEM0_PRIORITY"));
      cmd.addParameter("ITEM0_VALID_FROM");
      cmd.addParameter("ITEM0_VALID_TO");
      trans = mgr.perform(trans);

      trans.clear();
      q = trans.addQuery(itemblk1);
       //bug 58216 starts
       q.addWhereCondition("DISTRIBUTION_TEMP_SEQ = ?");
       q.addParameter("ITEM1_DISTRIBUTION_TEMP_SEQ",headset.getValue("HEAD_TEMP_SEQ"));
       //bug 58216 end
      q.includeMeta("ALL");
      trans= mgr.perform(trans);
      data  = trans.getBuffer("ITEM1");

      itemset1.clear();
      for (i=0;i<data.countItems()-1;i++)
      {
         itemset1.addRow(data.getBufferAt(i));
      }
   }

   public void  frame2NextGetData()
   {
      ASPManager mgr = getASPManager();
      ASPBuffer temp_buff,sub_Buff;

      if (frame2Populated)
      {
         trans.clear();

	 //Bug Id 75677, Start
	 //Get unique starting characters from the database for users/groups.
	 //Get unique starting characters from the database for users/groups.
	 String ITEM2_GROUP_PERSON_LIST;
	 String selected_char; 
	 ITEM2_GROUP_PERSON_LIST = ""; 

	 String sql_Select1="SELECT UNIQUE SUBSTR(NAME,0,1) FROM DOC_DIST_PERSON";
         q = trans.addQuery("LISTPERSON",sql_Select1);
	 q.setBufferSize(10000);//Make sure we don't run out of buffer size

         String sql_Select2="SELECT UNIQUE SUBSTR(GROUP_DESCRIPTION,0,1) FROM DOCUMENT_GROUP ";
         q = trans.addQuery("LISTGROUP",sql_Select2);
         q.setBufferSize(10000);//Make sure we don't run out of buffer size
	 trans = mgr.perform(trans);
	 
	 if (trans.getBufferAt(1).countItems() > 0) {
	     //get the persons sub buffer
	     sub_Buff = trans.getBufferAt(1);
	     //string concatenation of zeroth index values-for persons
	     temp_buff=sub_Buff.getBufferAt(0);
	     //string concatenation of the rest-for persons
	     for (int x=0; x<sub_Buff.countItems()-1; x++)
	     {
		temp_buff=sub_Buff.getBufferAt(x);
		selected_char = temp_buff.getValueAt(0).toUpperCase() + (char)31; 
		if (ITEM2_GROUP_PERSON_LIST.indexOf(selected_char) == -1) {
		    ITEM2_GROUP_PERSON_LIST=ITEM2_GROUP_PERSON_LIST+selected_char ;
		}       
	     }
	 }
	 if (trans.getBufferAt(2).countItems() > 0) {
	     //get the groups sub buffer
	     sub_Buff = trans.getBufferAt(2);
	     //string concatenation of the rest-for groups
	     for (int x=0; x<sub_Buff.countItems()-1; x++)
	     {
		temp_buff=sub_Buff.getBufferAt(x);
		selected_char = temp_buff.getValueAt(0).toUpperCase() + (char)31;
		if (ITEM2_GROUP_PERSON_LIST.indexOf(selected_char) == -1) {
		    ITEM2_GROUP_PERSON_LIST=ITEM2_GROUP_PERSON_LIST+selected_char ;
		}
	     }
	 }
	 data=mgr.newASPBuffer();
	 data.addItem("ITEM2_GROUP_PERSON_LIST", ITEM2_GROUP_PERSON_LIST);
	 itemset2.addRow(data);
	 //Bug Id 75677, End
         frame2Populated = false;
      }
      populateListBox = true;
   }


   public void  frame3NextGetData()
   {
      ASPManager mgr = getASPManager();

      itemset2.changeRow();
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_PERSON_LIST_0")))
         itemset2.setValue("ITEM2_PERSON_LIST_0",itemset2.getValue("ITEM2_PERSON_LIST_0")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_PERSON_LIST_1")))
         itemset2.setValue("ITEM2_PERSON_LIST_1",itemset2.getValue("ITEM2_PERSON_LIST_1")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_IDENTITY_LIST_0")))
         itemset2.setValue("ITEM2_IDENTITY_LIST_0",itemset2.getValue("ITEM2_IDENTITY_LIST_0")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_IDENTITY_LIST_1")))
         itemset2.setValue("ITEM2_IDENTITY_LIST_1",itemset2.getValue("ITEM2_IDENTITY_LIST_1")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_GROUP_DESC_0")))
         itemset2.setValue("ITEM2_GROUP_DESC_0",itemset2.getValue("ITEM2_GROUP_DESC_0")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_GROUP_DESC_1")))
         itemset2.setValue("ITEM2_GROUP_DESC_1",itemset2.getValue("ITEM2_GROUP_DESC_1")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_GROUP_ID_0")))
         itemset2.setValue("ITEM2_GROUP_ID_0",itemset2.getValue("ITEM2_GROUP_ID_0")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_GROUP_ID_1")))
         itemset2.setValue("ITEM2_GROUP_ID_1",itemset2.getValue("ITEM2_GROUP_ID_1")+strObj.valueOf((char)31));
      //Bug Id 75677, Start
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_SELECTED_CHARACTERS")))
	 itemset2.setValue("ITEM2_SELECTED_CHARACTERS",itemset2.getValue("ITEM2_SELECTED_CHARACTERS")+strObj.valueOf((char)31));
      //Bug Id 75677, End
      /*ASP2JAVA WARNING: Verify this condition.
      original condition
      if (tmpIdentityList!=itemset2.getValue("ITEM2_IDENTITY_LIST_1")+itemset2.getValue("ITEM2_GROUP_ID_1")) {*/

      if (!((itemset2.getValue("ITEM2_IDENTITY_LIST_1")+itemset2.getValue("ITEM2_GROUP_ID_1")).equals(tmpIdentityList)))
      {
         cmd = trans.addCustomCommand("INSTTEMPRECEI","DOC_DIST_ENGINE_API.Insert_Temp_Receivers");
         cmd.addParameter("HEAD_TEMP_SEQ",headset.getValue("HEAD_TEMP_SEQ"));
         cmd.addParameter("ITEM2_IDENTITY_LIST_1",itemset2.getValue("ITEM2_IDENTITY_LIST_1"));
         cmd.addParameter("ITEM2_GROUP_ID_1",itemset2.getValue("ITEM2_GROUP_ID_1"));
         trans = mgr.perform(trans);

         trans.clear();
         okFindITEM3();
         tmpIdentityList = itemset2.getValue("ITEM2_IDENTITY_LIST_1") + itemset2.getValue("ITEM2_GROUP_ID_1");
      }
   }


   public void  frame4NextGetData()
   {
      itemset4.clear();
      itemset5.clear();
      okFindITEM4();
      okFindITEM5();
   }


   public void  frame0PreviousGetData()
   {

   }


   public void  frame1PreviousGetData()
   {
      ASPManager mgr = getASPManager();

      itemset2.changeRow();
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_PERSON_LIST_0")))
         itemset2.setValue("ITEM2_PERSON_LIST_0",itemset2.getValue("ITEM2_PERSON_LIST_0")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_PERSON_LIST_1")))
         itemset2.setValue("ITEM2_PERSON_LIST_1",itemset2.getValue("ITEM2_PERSON_LIST_1")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_IDENTITY_LIST_0")))
         itemset2.setValue("ITEM2_IDENTITY_LIST_0",itemset2.getValue("ITEM2_IDENTITY_LIST_0")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_IDENTITY_LIST_1")))
         itemset2.setValue("ITEM2_IDENTITY_LIST_1",itemset2.getValue("ITEM2_IDENTITY_LIST_1")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_GROUP_DESC_0")))
         itemset2.setValue("ITEM2_GROUP_DESC_0",itemset2.getValue("ITEM2_GROUP_DESC_0")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_GROUP_DESC_1")))
         itemset2.setValue("ITEM2_GROUP_DESC_1",itemset2.getValue("ITEM2_GROUP_DESC_1")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_GROUP_ID_0")))
         itemset2.setValue("ITEM2_GROUP_ID_0",itemset2.getValue("ITEM2_GROUP_ID_0")+strObj.valueOf((char)31));
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_GROUP_ID_1")))
         itemset2.setValue("ITEM2_GROUP_ID_1",itemset2.getValue("ITEM2_GROUP_ID_1")+strObj.valueOf((char)31));
      //Bug Id 75677, Start
      if (!mgr.isEmpty(itemset2.getValue("ITEM2_SELECTED_CHARACTERS")))
	 itemset2.setValue("ITEM2_SELECTED_CHARACTERS",itemset2.getValue("ITEM2_SELECTED_CHARACTERS")+strObj.valueOf((char)31));
      //Bug Id 75677, End
   }


   public void  frame2PreviousGetData()
   {
      populateListBox = true;
   }


   public void  frame3PreviousGetData()
   {

   }


   public void  finish()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomCommand("INITDIST","DOC_DIST_ENGINE_API.Initialize_Distribution");
      cmd.addParameter("ITEM4_DIST_NUMBER");
      cmd.addParameter("HEAD_TEMP_SEQ",headset.getValue("HEAD_TEMP_SEQ"));
      trans = mgr.perform(trans);
      String distNumber = trans.getValue("INITDIST/DATA/ITEM4_DIST_NUMBER");
      itemset4.setValue("ITEM4_DIST_NUMBER",distNumber);
      itemset4.changeRow();
      trans.clear();
      cmd = trans.addCustomCommand("REMTEMPDIST","DOC_DIST_ENGINE_API.Remove_Temp_Distribution");
      cmd.addParameter("HEAD_TEMP_SEQ",headset.getValue("HEAD_TEMP_SEQ"));
      trans = mgr.perform(trans);

      if ("TRUE".equals(mgr.readValue("ITEM4_EXECUTE_IMMEDIATELY")))
      {
         trans.clear();
         cmd = trans.addCustomCommand("EXECDIST","DOC_DIST_ENGINE_API.Execute_Distribution");
         cmd.addParameter("ITEM4_DUMMY");
         cmd.addParameter("ITEM4_DIST_NUMBER",distNumber);
         cmd.addParameter("ITEM4_DUMMY","0");
         cmd.addParameter("ITEM5_DUMMY","1");
         trans = mgr.perform(trans);
      }
      cancel();
   }


   public void  cancel()
   {
      // Close the window and refresh parent.
      bCloseWindow = true;
   }


   public void  okFindITEM3()
   {
      int items;
      ASPManager mgr = getASPManager();
      ASPBuffer itemBuffer;

      itemset3.clear();
      trans.clear();
      q = trans.addEmptyQuery(itemblk3);
      //bug 58216 starts
      q.addWhereCondition("DISTRIBUTION_TEMP_SEQ = ?");
      q.addParameter("ITEM1_DISTRIBUTION_TEMP_SEQ",headset.getValue("HEAD_TEMP_SEQ"));
      //bug 58216 end
      q.includeMeta("ALL");
      trans = mgr.validate(trans);
      itemBuffer = trans.getBuffer("ITEM3");
      items = itemBuffer.countItems();

      for (i=0;i<items-1;i++)
      {
         data = itemBuffer.getBufferAt(i);
         itemset3.addRow(data);
      }
      trans.clear();
   }


   public void  saveReturnITEM3()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomCommand("MODIFY3","DOC_DIST_TEMP_REC_API.Modify__");
      cmd.addParameter("ITEM3_INFO");
      cmd.addParameter("ITEM3_OBJID",mgr.readValue("ITEM3_OBJID"));
      cmd.addParameter("ITEM3_OBJVERSION",mgr.readValue("ITEM3_OBJVERSION"));
      cmd.addParameter("ITEM3_ATTR","DOC_DIST_METHOD"+strObj.valueOf((char)31)+mgr.readValue("ITEM3_DOC_DIST_METHOD")+strObj.valueOf((char)30));
      cmd.addParameter("ITEM3_ACTION","DO");
      trans = mgr.perform(trans);
      trans.clear();
      currow = itemset3.getCurrentRowNo();
      okFindITEM3();
      itemset3.goTo(currow);
   }


   public void  cancelEditITEM3()
   {
      itemlay3.setLayoutMode(itemlay3.getHistoryMode());
   }


   public void  okFindITEM4()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addEmptyQuery(itemblk4);
      //bug 58216 starts
      q.addWhereCondition("DISTRIBUTION_TEMP_SEQ = ?");
      q.addParameter("ITEM1_DISTRIBUTION_TEMP_SEQ",headset.getValue("HEAD_TEMP_SEQ"));
      //bug 58216 end
      q.includeMeta("ALL");
      trans = mgr.validate(trans);

      data = trans.getBuffer("ITEM4/DATA");
      itemset4.addRow(data);
      if (itemset4.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDNODATAFOUND: No data found"));
         itemset4.clear();
      }
   }


   public void  okFindITEM5()
   {
      int items;
      ASPManager mgr = getASPManager();
      ASPBuffer itemBuffer;

      trans.clear();
      q = trans.addEmptyQuery(itemblk5);
      //bug 58216 starts
      q.addWhereCondition("DISTRIBUTION_TEMP_SEQ = ?");
      q.addParameter("ITEM1_DISTRIBUTION_TEMP_SEQ",headset.getValue("HEAD_TEMP_SEQ"));
      //bug 58216 end
      q.includeMeta("ALL");
      trans = mgr.validate(trans);

      itemBuffer = trans.getBuffer("ITEM5");
      items = itemBuffer.countItems();

      for (i=0;i<items-1;i++)
      {
         data = itemBuffer.getBufferAt(i);
         itemset5.addRow(data);
      }
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

      disableConfiguration();
      //Bug Id 78998, start
      disableOptions();
      disableHomeIcon();
      disableNavigate();
      //Bug Id 78998, end

      headblk = mgr.newASPBlock("HEAD");
      f = headblk.addField("HEAD_TEMP_SEQ");

      f = headblk.addField("HEAD_DOC_CLASS");
      f.setSize(12);
      f.setMaxLength(12);
      f.setReadOnly();
      f.setUpperCase();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDHEADDOCCLASS: Doc Class");

      f = headblk.addField("HEAD_DOC_NO");
      f.setSize(24);
      f.setMaxLength(24);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDHEADDOCNO: Doc Number");

      //prsalk
      f = headblk.addField("HEAD_DOC_SHEET");
      f.setSize(12);
      f.setMaxLength(12);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDHEADDOCSHEET: Doc Sheet");


      f = headblk.addField("HEAD_DOC_REV");
      f.setSize(24);
      f.setMaxLength(24);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDHEADDOCREV: Doc Revision");

      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      headbar.disableCommand(headbar.FIND);
      headbar.disableCommand(headbar.BACK);
      headbar.disableCommand(headbar.FORWARD);
      headbar.disableCommand(headbar.BACKWARD);
      headbar.disableCommand(headbar.NEWROW);
      headbar.disableCommand(headbar.NEWROW);
      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.SINGLE_LAYOUT);
      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 0 ---------------------------------------
      //---------------------------------------------------------------------
      itemblk0 = mgr.newASPBlock("ITEM0");
      f = itemblk0.addField("ITEM0_TRIG_COND");
      f.setSize(20);
      f.setMaxLength(200);
      f.setMandatory();
      f.setSelectBox();
      f.enumerateValues("DOC_DIST_TRIGGER_COND_API");
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM0TRIGCOND: Triggering Condition:");

      f = itemblk0.addField("ITEM0_TYPE");
      f.setSize(20);
      f.setMaxLength(200);
      f.setMandatory();
      f.setSelectBox();
      f.enumerateValues("DOC_DIST_TYPE_API");
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM0TYPE: Type:");

      f = itemblk0.addField("ITEM0_PRIORITY");
      f.setSize(20);
      f.setMaxLength(200);
      f.setMandatory();
      f.setSelectBox();
      f.enumerateValues("DOC_DIST_PRIORITY_API");
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM0PRIORITY: Priority:");

      f = itemblk0.addField("ITEM0_VALID_FROM","Date");
      f.setSize(12);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM0VALIDFROM: Valid From:");
      f = itemblk0.addField("ITEM0_VALID_TO","Date");
      f.setSize(12);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM0VALIDTO: Valid To:");

      f = itemblk0.addField("ITEM0_NO_OF_COPIES");
      f.setSize(5);
      f.setMaxLength(20);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM0NOOFCOPIES: Number of Copies:");

      f = itemblk0.addField("ITEM0_DIST_NOTE");
      f.setSize(50);
      f.setHeight(4);
      f.setMaxLength(200);
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM0DISTNOTE: Distribution Note:");
      itemset0 = itemblk0.getASPRowSet();
      itembar0 = mgr.newASPCommandBar(itemblk0);
      itemtbl0 = mgr.newASPTable(itemblk0);
      itemtbl0.setTitle(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM0DISTSETTINGS: Distribution Settings"));
      itemlay0 = itemblk0.getASPBlockLayout();
      itemlay0.setDialogColumns(1);
      itemlay0.setDefaultLayoutMode(itemlay0.EDIT_LAYOUT);
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

      f = itemblk1.addField("ITEM1_DOC_CLASS");
      f.setDbName("DOC_CLASS");
      f.setSize(12);
      f.setMaxLength(12);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM1DOCCLASS: Document Class");

      f = itemblk1.addField("ITEM1_DOC_NO");
      f.setDbName("DOC_NO");
      f.setSize(24);
      f.setMaxLength(120);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM1DOCNO: Document No.");
      //prsalk
      f = itemblk1.addField("ITEM1_DOC_SHEET");
      f.setDbName("DOC_SHEET");
      f.setSize(24);
      f.setMaxLength(120);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM1DOCSHEET: Doc Sheet.");


      f = itemblk1.addField("ITEM1_DOC_REV");
      f.setDbName("DOC_REV");
      f.setSize(6);
      f.setMaxLength(6);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM1DOCREV: Document Revision");

      f = itemblk1.addField("ITEM1_NUMBER_OF_COPIES");
      f.setDbName("NUMBER_OF_COPIES");
      f.setSize(5);
      f.setMaxLength(20);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM1NUMBEROFCOPIES: No. of Copies");

      f = itemblk1.addField("ITEM1_DATE_VALID_FROM","Date");
      f.setDbName("DATE_VALID_FROM");
      f.setSize(12);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM1DATEVALIDFROM: Valid From");

      f = itemblk1.addField("ITEM1_DATE_VALID_TO","Date");
      f.setDbName("DATE_VALID_TO");
      f.setSize(12);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM1DATEVALIDTO: Valid To");

      f = itemblk1.addField("ITEM1_SENDER_PERSON");
      f.setDbName("SENDER_PERSON");
      f.setSize(20);
      f.setMaxLength(30);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM1SENDERPERSON: Sender");

      f = itemblk1.addField("ITEM1_DISTRIBUTION_TEMP_SEQ");
      f.setDbName("DISTRIBUTION_TEMP_SEQ");
      f.setHidden();
      f.setSize(2);
      f.setMaxLength(200);
      f.setInsertable();

      f = itemblk1.addField("ITEM1_LINE_NO");
      f.setDbName("LINE_NO");
      f.setHidden();
      f.setSize(2);
      f.setMaxLength(200);
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM1LINENO: Line No.");

      f = itemblk1.addField("ITEM1_FILE_TYPE");
      f.setDbName("FILE_TYPE");
      f.setHidden();
      f.setSize(2);
      f.setMaxLength(200);
      f.setInsertable();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM1FILETYPE: File Type");

      f = itemblk1.addField("ITEM1_DISTRIBUTION_NOTE");
      f.setDbName("DISTRIBUTION_NOTE");
      f.setHidden();
      f.setSize(6);
      f.setHeight(4);
      f.setMaxLength(200);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM1DISTRIBUTIONNOTE: Distribution Note");
      itemblk1.setView("DOC_DIST_TEMP");
      itemblk1.setMasterBlock(headblk);
      itemblk1.defineCommand("DOC_DIST_TEMP_API","New__,Modify__,Remove__");
      itemset1 = itemblk1.getASPRowSet();
      itembar1 = mgr.newASPCommandBar(itemblk1);
      // itembar1.setWidth(0);
      // itembar1.setInnerWidth(560);
      itembar1.disableCommand(itembar1.DUPLICATEROW);
      itembar1.disableCommand(itembar1.DELETE);
      itembar1.disableCommand(itembar1.NEWROW);
      itembar1.disableCommand(itembar1.EDITROW);
      itemtbl1 = mgr.newASPTable(itemblk1);
      itemtbl1.setTitle(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM1TODIST: Documents to Distribute"));
      itemlay1 = itemblk1.getASPBlockLayout();
      itemlay1.setDefaultLayoutMode(itemlay1.MULTIROW_LAYOUT);
      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 2 ---------------------------------------
      //---------------------------------------------------------------------
      itemblk2 = mgr.newASPBlock("ITEM2");
      itemblk2.addField("ITEM2_PERSON_LIST_0").
      setHidden().
      setSize(50).
      setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM2PERSONLIST0: Person List");
      itemblk2.addField("ITEM2_PERSON_LIST_1").
      setHidden().
      setSize(50).
      setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM2PERSONLIST1: Selected Person List");
      itemblk2.addField("ITEM2_IDENTITY_LIST_0").
      setHidden().
      setSize(50).
      setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM2IDENTITYLIST0: Identity List");
      itemblk2.addField("ITEM2_IDENTITY_LIST_1").
      setHidden().
      setSize(50).
      setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM2IDENTITYLIST1: Selected Identity List");
      itemblk2.addField("ITEM2_GROUP_DESC_0").
      setHidden().
      setSize(50).
      setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM2GROUPDESC0: Group Desc List");
      itemblk2.addField("ITEM2_GROUP_DESC_1").
      setHidden().
      setSize(50).
      setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM2GROUPDESC1: Selected Group Desc List");
      itemblk2.addField("ITEM2_GROUP_ID_0").
      setHidden().
      setSize(50).
      setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM2GROUPID0: Group ID List");
      itemblk2.addField("ITEM2_GROUP_ID_1").
      setHidden().
      setSize(50).
      setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM2GROUPID1: Selected Group ID List");
      
      //Bug Id 75677, Start 
      itemblk2.addField("ITEM2_SELECTED_CHARACTER").
      setHidden().
      setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM2SELCHAR: Selected Character");
      
      itemblk2.addField("ITEM2_SELECTED_CHARACTERS").
      setHidden().
      setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM2SELCHARS: Selected Characters");
      
      itemblk2.addField("ITEM2_GROUP_PERSON_LIST").
      setHidden().
      setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM2CHARLIST: Character List");

      itemblk2.addField("DUMMY").
      setHidden();
      //Bug Id 75677, End
      itemset2 = itemblk2.getASPRowSet();
      itembar2 = mgr.newASPCommandBar(itemblk2);
      itemtbl2 = mgr.newASPTable(itemblk2);
      itemtbl2.setTitle(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM2SELECTRCVRS: Select Receivers"));
      itemlay2 = itemblk2.getASPBlockLayout();
      itemlay2.setDefaultLayoutMode(itemlay2.EDIT_LAYOUT);
      itemlay2.showBottomLine(false);//Bug Id 75677
      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 3 ---------------------------------------
      //---------------------------------------------------------------------
      itemblk3 = mgr.newASPBlock("ITEM3");

      itemblk3.addField("ITEM3_OBJID").
      setDbName("OBJID").
      setHidden();

      itemblk3.addField("ITEM3_OBJVERSION").
      setDbName("OBJVERSION").
      setHidden();

      f = itemblk3.addField("ITEM3_DISTRIBUTION_TEMP_SEQ");
      f.setDbName("DISTRIBUTION_TEMP_SEQ");
      f.setHidden();
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM3DISTRIBUTIONTEMPSEQ: Temp. Seq.");

      f = itemblk3.addField("ITEM3_LINE_NO");
      f.setDbName("LINE_NO");
      f.setHidden();
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM3LINENO: Line No.");

      f = itemblk3.addField("ITEM3_RECEIVER_PERSON");
      f.setDbName("RECEIVER_PERSON");
      f.setSize(20);
      f.setMaxLength(30);
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM3RECEIVERPERSON: Receiver");

      f = itemblk3.addField("ITEM3_RECEIVER_PERSON_NAME");
      f.setSize(20);
      f.setMaxLength(20);
      f.setReadOnly();
      f.setFunction("DOC_DIST_PERSON_API.Get_Name(:ITEM3_RECEIVER_PERSON)");
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM3RECEIVERPERSONNAME: Name of Receiver");

      f = itemblk3.addField("ITEM3_RECEIVER_GROUP");
      f.setDbName("RECEIVER_GROUP");
      f.setSize(20);
      f.setMaxLength(20);
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM3RECEIVERGROUP: Group");

      f = itemblk3.addField("ITEM3_RECEIVER_GROUP_NAME");
      f.setSize(20);
      f.setMaxLength(20);
      f.setReadOnly();
      f.setFunction("DOCUMENT_GROUP_API.Get_Group_Description(:ITEM3_RECEIVER_GROUP)");
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM3RECEIVERGROUPNAME: Group Desc.");

      f = itemblk3.addField("ITEM3_DOC_DIST_METHOD");
      f.setDbName("DOC_DIST_METHOD");
      f.setSize(20);
      f.setMaxLength(200);
      f.setSelectBox();
      f.setMandatory();
      f.enumerateValues("DOC_DIST_METHOD_API");
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM3DOCDISTMETHOD: Distribution Method");

      f = itemblk3.addField("ITEM3_INFO");
      f.setFunction("''");
      f.setHidden();

      f = itemblk3.addField("ITEM3_ATTR");
      f.setFunction("''");
      f.setHidden();

      f = itemblk3.addField("ITEM3_ACTION");
      f.setFunction("''");
      f.setHidden();
      itemblk3.setView("DOC_DIST_TEMP_REC");
      itemblk3.defineCommand("DOC_DIST_TEMP_REC_API","New__,Modify__,Remove__");
      itemset3 = itemblk3.getASPRowSet();
      itembar3 = mgr.newASPCommandBar(itemblk3);
      // itembar3.setWidth(0);
      // itembar3.setInnerWidth(560);
      itembar3.disableCommand(itembar3.FIND);
      itembar3.disableCommand(itembar3.COUNTFIND);
      itembar3.disableCommand(itembar3.NEWROW);
      itembar3.disableCommand(itembar3.DELETE);
      itembar3.disableCommand(itembar3.DUPLICATEROW);
      itembar3.defineCommand(itembar3.SAVERETURN,"saveReturnITEM3");
      itembar3.defineCommand(itembar3.CANCELEDIT,"cancelEditITEM3");
      itemtbl3 = mgr.newASPTable(itemblk3);
      itemtbl3.setTitle(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM3DISTMETHODS: Distribution Methods"));
      itemlay3 = itemblk3.getASPBlockLayout();
      itemlay3.setDefaultLayoutMode(itemlay3.MULTIROW_LAYOUT);
      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 4 ---------------------------------------
      //---------------------------------------------------------------------
      itemblk4 = mgr.newASPBlock("ITEM4");

      f = itemblk4.addField("ITEM4_OBJID");
      f.setHidden();
      f.setDbName("OBJID");

      f = itemblk4.addField("ITEM4_OBJVERSION");
      f.setHidden();
      f.setDbName("OBJVERSION");

      f = itemblk4.addField("ITEM4_TRIG_COND");
      f.setDbName("DOC_DIST_TRIGGER_COND");
      f.setSize(20);
      f.setMaxLength(200);
      f.setMandatory();
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM4TRIGCOND: Triggering Condition:");

      f = itemblk4.addField("ITEM4_VALID_FROM","Date");
      f.setDbName("DATE_VALID_FROM");
      f.setReadOnly();
      f.setSize(12);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM4VALIDFROM: Valid From:");

      f = itemblk4.addField("ITEM4_DOC_DIST_TYPE");
      f.setDbName("DOC_DIST_TYPE");
      f.setSize(20);
      f.setMaxLength(200);
      f.setMandatory();
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM4DOCDISTTYPE: Type:");

      f = itemblk4.addField("ITEM4_VALID_TO","Date");
      f.setDbName("DATE_VALID_TO");
      f.setSize(12);
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM4VALIDTO: Valid To:");

      f = itemblk4.addField("ITEM4_PRIORITY");
      f.setDbName("DOC_DIST_PRIORITY");
      f.setSize(20);
      f.setMaxLength(200);
      f.setMandatory();
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM4PRIORITY: Priority:");

      f = itemblk4.addField("ITEM4_DISTRIBUTION_TEMP_SEQ");
      f.setDbName("DISTRIBUTION_TEMP_SEQ");
      f.setHidden();
      f.setMaxLength(10);
      f.setInsertable();
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM4DISTRIBUTIONTEMPSEQ: Line No.");

      f = itemblk4.addField("ITEM4_NO_OF_COPIES");
      f.setDbName("NUMBER_OF_COPIES");
      f.setSize(5);
      f.setMaxLength(20);
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM4NOOFCOPIES: Number of Copies:");

      f = itemblk4.addField("ITEM4_DIST_NOTE");
      f.setDbName("DISTRIBUTION_NOTE");
      f.setSize(20);
      f.setHeight(4);
      f.setMaxLength(200);
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM4DISTNOTE: Distribution Note:");

      f = itemblk4.addField("ITEM4_EXECUTE_IMMEDIATELY");
      f.setDbName("EXECUTE_IMMEDIATELY");
      f.setFunction("''");
      f.setCheckBox("FALSE,TRUE");
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM4EXECUTEIMMEDIATELY: Execute Immediately:");

      f = itemblk4.addField("ITEM4_DIST_NUMBER");
      f.setFunction("''");
      f.setHidden();

      f = itemblk4.addField("ITEM4_DUMMY");
      f.setFunction("''");
      f.setHidden();

      f = itemblk4.addField("ITEM5_DUMMY");
      f.setFunction("''");
      f.setHidden();
      itemblk4.setView("DOC_DIST_TEMP");
      itemblk4.defineCommand("DOC_DIST_TEMP_API","New__,Modify__,Remove__");
      itemset4 = itemblk4.getASPRowSet();
      itembar4 = mgr.newASPCommandBar(itemblk4);
      itemtbl4 = mgr.newASPTable(itemblk4);
      itemtbl4.setTitle(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM4APPTEMPLDETTITLE: Approval Template Detail"));
      itemlay4 = itemblk4.getASPBlockLayout();
      itemlay4.setDialogColumns(2);
      itemlay4.setSpaceAfter(1);
      itemlay4.setSpaceBetween(1);
      itemlay4.setDefaultLayoutMode(itemlay4.EDIT_LAYOUT);
      //---------------------------------------------------------------------
      //-------------- ITEM BLOCK - 5 ---------------------------------------
      //---------------------------------------------------------------------
      itemblk5 = mgr.newASPBlock("ITEM5");

      f = itemblk5.addField("ITEM5_DISTRIBUTION_TEMP_SEQ");
      f.setDbName("DISTRIBUTION_TEMP_SEQ");
      f.setHidden();
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM5DISTRIBUTIONTEMPSEQ: Temp. Seq.");

      f = itemblk5.addField("ITEM5_DOC_CLASS");
      f.setDbName("DOC_CLASS");
      f.setSize(12);
      f.setMaxLength(12);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM5DOCCLASS: Doc Class");

      f = itemblk5.addField("ITEM5_DOC_NO");
      f.setDbName("DOC_NO");
      f.setSize(24);
      f.setMaxLength(520);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM5DOCNO: Doc No");
      //prsalk
      f = itemblk5.addField("ITEM5_DOC_SHEET");
      f.setDbName("DOC_SHEET");
      f.setSize(24);
      f.setMaxLength(520);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM5DOCSHEET: Doc Sheet");

      f = itemblk5.addField("ITEM5_DOC_REV");
      f.setDbName("DOC_REV");
      f.setSize(6);
      f.setMaxLength(6);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM5DOCREV: Doc Rev");

      f = itemblk5.addField("ITEM5_RECEIVER_PERSON");
      f.setDbName("RECEIVER_PERSON");
      f.setSize(20);
      f.setMaxLength(30);
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM5RECEIVERPERSON: Receiver");

      f = itemblk5.addField("ITEM5_RECEIVER_PERSON_NAME");
      f.setSize(20);
      f.setMaxLength(20);
      f.setReadOnly();
      f.setFunction("DOC_DIST_PERSON_API.Get_Name(:ITEM5_RECEIVER_PERSON)");
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM5RECEIVERPERSONNAME: Name of Receiver");

      f = itemblk5.addField("ITEM5_RECEIVER_GROUP");
      f.setDbName("RECEIVER_GROUP");
      f.setSize(20);
      f.setMaxLength(20);
      f.setReadOnly();
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM5RECEIVERGROUP: Group");

      f = itemblk5.addField("ITEM5_RECEIVER_GROUP_NAME");
      f.setSize(20);
      f.setMaxLength(20);
      f.setReadOnly();
      f.setFunction("DOCUMENT_GROUP_API.Get_Group_Description(:ITEM3_RECEIVER_GROUP)");
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM5RECEIVERGROUPNAME: Group Desc.");

      f = itemblk5.addField("ITEM5_DOC_DIST_METHOD");
      f.setDbName("DOC_DIST_METHOD");
      f.setSize(20);
      f.setMaxLength(200);
      f.setLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM5DOCDISTMETHOD: Distribution Method");
      itemblk5.setView("DOC_DIST_TEMP_LIST");
      itemblk5.defineCommand("DOC_DIST_TEMP_LIST_API","New__,Modify__,Remove__");
      itemset5 = itemblk5.getASPRowSet();
      itembar5 = mgr.newASPCommandBar(itemblk5);
      // itembar5.setWidth(0);
      // itembar5.setInnerWidth(560);
      itembar5.disableCommand(itembar5.FIND);
      itembar5.disableCommand(itembar5.COUNTFIND);
      itembar5.disableCommand(itembar5.NEWROW);
      itembar5.disableCommand(itembar5.DELETE);
      itembar5.disableCommand(itembar5.DUPLICATEROW);
      itembar5.disableCommand(itembar5.EDITROW);
      itemtbl5 = mgr.newASPTable(itemblk5);
      itemtbl5.setTitle(mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDITEM5TBLDISTMETHOD: Distribution Methods"));
      itemlay5 = itemblk5.getASPBlockLayout();
      itemlay5.setDialogColumns(1);
      itemlay5.setDefaultLayoutMode(itemlay5.MULTIROW_LAYOUT);
   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWDOCUMENTDISTRIBUTIONWIZARDTITLE: Document Distribution Wizard";
   }

   protected String getTitle()
   {
      return "DOCMAWDOCUMENTDISTRIBUTIONWIZARDTITLE: Document Distribution Wizard";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML("<table border=0 bgcolor=white class=\"BlockLayoutTable\" cellspacing=0 cellpadding=2 cols=4 width=\"727\">\n");
      appendToHTML("   <tr valign=\"top\" align=\"left\">\n");
      appendToHTML("       <td nowrap colspan=\"1\" rowspan=\"4\" align=\"left\" valign=\"top\"><img src = \"images/DistributeDocuments.JPG\"></td>\n");
      if (activeFrame0)
      {
         appendToHTML("       <td colspan=1 rowspan=1 align=\"left\" valign=\"top\" height=\"45\">");
         appendToHTML(fmt.drawWriteLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDGENDISTSETTINGS: Step 1 - General Distribution Settings"));
         appendToHTML("</td>\n");
         appendToHTML(" </tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\"><td colspan=3 rowspan=1 align=\"left\" valign=\"top\">");
         appendToHTML(itemlay0.generateDialog());
         appendToHTML("</td></tr>\n");
         appendToHTML("       <tr></tr>\n");
         appendToHTML("       <tr></tr>\n");
         appendToHTML("       <tr><td colspan=4><hr width=\"100%\"></td></tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\">\n");
         appendToHTML("       <td colspan=4 rowspan=1 valign=\"top\" align=\"right\">\n");
         appendToHTML(fmt.drawSubmit("NEXT0",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDNEXT:  Next  > "),""));
         appendToHTML("&nbsp;\n");
         appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDCANCEL: Cancel "),""));
         appendToHTML("&nbsp;&nbsp;\n");
         appendToHTML("       </td>\n");
         appendToHTML("       </tr>\n");
      }
      if (activeFrame1)
      {
         appendToHTML("       <td colspan=3 rowspan=1 align=\"left\" valign=\"top\" height=\"30\">");
         appendToHTML(fmt.drawWriteLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDDOCSTODIST: Step 2 - Document(s) to Distribute"));
         appendToHTML("</td>\n");
         appendToHTML(" </tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\"><td colspan=3 rowspan=1 align=\"left\" valign=\"top\">");
         appendToHTML(itemlay1.show());
         appendToHTML("</td></tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\"><td colspan=3 rowspan=1 align=\"left\" valign=\"top\"></td>\n");
         appendToHTML("       </tr>\n");
         appendToHTML("       <tr></tr>\n");
         appendToHTML("       <tr><td colspan=4><hr width=\"100%\"></td></tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\">\n");
         appendToHTML("       <td colspan=4 rowspan=1 valign=\"top\" align=\"right\">\n");
         appendToHTML(fmt.drawSubmit("BACK1",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDBACK: <  Back  "),""));
         appendToHTML("&nbsp;&nbsp;");
         appendToHTML(fmt.drawSubmit("NEXT1",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDNEXT:  Next  > "),""));
         appendToHTML("&nbsp;\n");
         appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDCANCEL: Cancel "),""));
         appendToHTML("&nbsp;&nbsp;\n");
         appendToHTML("       </td>\n");
         appendToHTML("       </tr>\n");
      }
      if (activeFrame2)
      {
         
         //Bug Id 75677, Start
	  String[] Person_group_list =  itemset2.getValue("ITEM2_GROUP_PERSON_LIST").split(strObj.valueOf((char)31));
	  Arrays.sort(Person_group_list);
         //Bug Id 75677, End   
	 appendToHTML("       <td colspan=1 rowspan=1 valign=\"top\" height=\"30\">");
         appendToHTML(fmt.drawWriteLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDSELECTRECEIVERS: Step 3 - Select Receiver(s)"));
         appendToHTML("</br></td>\n");
         appendToHTML(" </tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\"><td align=\"left\" colspan=\"1\" rowspan=\"1\">");
         appendToHTML(itemlay2.generateDialog());
         appendToHTML("</td></tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\"><td align=\"left\" valign=\"top\" colspan=\"1\" rowspan=\"1\">"); //Bug Id 75677
         //Bug 58526, Start, Removed text translation
         appendToHTML(fmt.drawWriteLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDAVAILABLEPERSONS: Available Persons and Groups:")); //Bug Id 78998
         //Bug 58526, End
	 //Bug Id 75677, Start
	 appendToHTML("<br>\n");
         for (int k=0;k<Person_group_list.length;k++)
	 {
	     printLink(Person_group_list[k],"javascript:populateByCharacter ('"+mgr.encodeStringForJavascript(Person_group_list[k])+"')");
	     appendToHTML("&nbsp;\n");
	  
	 }
         appendToHTML("</td><td width=100%></td><td align=\"left\" valign=\"top colspan=\"1\" rowspan=\"1\">");
         //Bug Id 75677, End
	 //Bug 58526, Start, Removed text translation
         appendToHTML(fmt.drawWriteLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDRECEIVERS: Receivers:"));
         //Bug 58526, End
         appendToHTML("</td></tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\">\n");
         appendToHTML("          <td align=\"left\" colspan=1 rowspan=1 valign=\"top\" >");
         appendToHTML(fmt.drawSelectStart("col_0", "style=\"width:225px;\" multiple size=12")); //Bug Id 75677
         appendToHTML("<option>__________________________");
         appendToHTML(fmt.drawSelectEnd());
         appendToHTML("</td>\n");
         appendToHTML("          <td align=\"left\" colspan=1 rowspan=1 valign=\"middle\">\n");
         appendToHTML("             <table align=\"center\" border=0 bgcolor=white class=\"BlockLayoutTable\" cellspacing=0 cellpadding=0>\n");
	 //Bug Id 75677, Start
         appendToHTML("                <tr><td align=\"center\" ><P>");
	 appendToHTML(                 fmt.drawButton("ADD","&nbsp;&nbsp;" + mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDADD: Add")+"&nbsp;&nbsp;","onClick='add()'"));
	 appendToHTML("</P></td></tr>\n");
         appendToHTML("                <tr><td>&nbsp;</td></tr>\n");
         appendToHTML("                <tr><td>&nbsp;</td></tr>\n");
         appendToHTML("                <tr></tr>\n");
         appendToHTML("                <tr><td align=\"center\" ><P>");
	 appendToHTML(                 fmt.drawButton("REMOVE",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDREMOVE: Remove"),"onClick='remove()'"));
	 appendToHTML("</P></td></tr>\n");
	 //Bug Id 75677, End
         appendToHTML("             </table>\n");
         appendToHTML("          </td>\n");
         appendToHTML("          <td align=\"left\" colspan=1 rowspan=1 valign=\"top\">");
         appendToHTML(fmt.drawSelectStart("col_1", "style=\"width:225px;\" multiple size=12")); //Bug Id 75677
         appendToHTML("<option>__________________________");
         appendToHTML(fmt.drawSelectEnd());
         appendToHTML("</td>\n");
         appendToHTML("       </tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\">\n");
         appendToHTML("          <td></td>\n");
         appendToHTML("          <td>\n");
         appendToHTML(fmt.drawCheckbox("GROUPS","",true,"onClick='populateSelectBox()'"));
         //Bug 58526, Start, Removed text translation
         appendToHTML(fmt.drawWriteLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDSHOWGROUPS: Show Groups"));
         //Bug 58526, End
         appendToHTML("<br>\n");
         appendToHTML(fmt.drawCheckbox("USERS","",true,"onClick='populateSelectBox()'"));
         //Bug 58526, Start, Removed text translation
         appendToHTML(fmt.drawWriteLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDSHOWUSERS: Show Persons"));
         //Bug 58526, End
         appendToHTML("          </td>\n");
         appendToHTML("       </tr>\n");
         appendToHTML("          <tr><td colspan=4><hr width=\"100%\"></td></tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\">\n");
         appendToHTML("       <td colspan=4 rowspan=1 valign=\"top\" align=\"right\">\n");
         appendToHTML(fmt.drawSubmit("BACK2",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDBACK: <  Back  "),""));
         appendToHTML("&nbsp;&nbsp;");
         appendToHTML(fmt.drawSubmit("NEXT2",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDNEXT:  Next  > "),""));
         appendToHTML("&nbsp;\n");
         appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDCANCEL: Cancel "),""));
         appendToHTML("&nbsp;&nbsp;\n");
         appendToHTML("       </td>\n");
         appendToHTML("       </tr>\n");
      }
      if (activeFrame3)
      {
         appendToHTML("       <td colspan=3 rowspan=1 align=\"left\" valign=\"top\" height=\"30\">");
         appendToHTML(fmt.drawWriteLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDSELECTDISTMETHOD: Step 4 - Select Distribution Method(s)"));
         appendToHTML("</td>\n");
         appendToHTML(" </tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\"><td colspan=3 rowspan=1 align=\"left\" valign=\"top\">");
         appendToHTML(itemlay3.show());
         appendToHTML("</td></tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\"><td colspan=3 rowspan=1 align=\"left\" valign=\"top\"></td></tr>\n");
         appendToHTML("       <tr></tr>\n");
         appendToHTML("       <tr><td colspan=4 align=\"left\"><hr width=\"100%\"></td></tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"right\">\n");
         appendToHTML("       <td colspan=4 rowspan=1 valign=\"top\" align=\"right\">\n");
         appendToHTML(fmt.drawSubmit("BACK3",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDBACK: <  Back  "),""));
         appendToHTML("&nbsp;&nbsp;");
         appendToHTML(fmt.drawSubmit("NEXT3",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDNEXT:  Next  > "),""));
         appendToHTML("&nbsp;\n");
         appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDCANCEL: Cancel "),""));
         appendToHTML("&nbsp;&nbsp;\n");
         appendToHTML("       </td>\n");
         appendToHTML("       </tr>\n");
      }
      if (activeFrame4)
      {
         appendToHTML("       <td colspan=3 rowspan=1 valign=\"top\" height=\"30\">");
         appendToHTML(fmt.drawWriteLabel("DOCMAWDOCUMENTDISTRIBUTIONWIZARDFINALSETTINGS: Step 5 - Final Settings"));
         appendToHTML("</td>\n");
         appendToHTML(" </tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\"><td colspan=3 rowspan=1 valign=\"top\" align=\"left\">");
         appendToHTML(itemlay4.generateDialog());
         appendToHTML("</td>\n");
         appendToHTML("       </tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\"><td colspan=3 rowspan=1 valign=\"top\" align=\"left\">");
         appendToHTML(itemlay5.show());
         appendToHTML("</td>\n");
         appendToHTML("       </tr>\n");
         appendToHTML("       <tr></tr>\n");
         appendToHTML("       <tr></tr>\n");
         appendToHTML("       <tr><td colspan=4 align=\"left\"><hr width=\"100%\"></td></tr>\n");
         appendToHTML("       <tr valign=\"top\" align=\"left\">\n");
         appendToHTML("       <td nowrap colspan=4 rowspan=1 valign=\"top\" align=\"right\">\n");
         appendToHTML(fmt.drawSubmit("BACK4",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDBACK: <  Back  "),""));
         appendToHTML("&nbsp;&nbsp;");
         appendToHTML(fmt.drawSubmit("FINISH",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDFINISH:   Finish  "),""));
         appendToHTML("&nbsp;\n");
         appendToHTML(fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWDOCUMENTDISTRIBUTIONWIZARDCANCEL: Cancel "),""));
         appendToHTML("&nbsp;&nbsp;\n");
         appendToHTML("       </td>\n");
         appendToHTML("       </tr>\n");
      }
      appendToHTML("</table>\n");
      appendToHTML("<script LANGUAGE=\"JavaScript\">\n");
      appendToHTML("//=============================================================================\n");
      appendToHTML("//   CLIENT FUNCTIONS\n");
      appendToHTML("//=============================================================================\n");
      if (populateListBox)
      {
         appendToHTML(" populateSelectBox();\n");
         appendToHTML(" populateSelectReceivers();\n");
      }
      if (bCloseWindow)
      {
         appendToHTML("  eval(\"opener.refreshParentRowsSelected()\");\n");
         appendToHTML("  window.close();\n");
      }

      //Bug Id 78998, start
      appendToHTML("function ascendingOrder(a, b) {\n");
      appendToHTML(" var x = String(a).toLowerCase(); \n");
      appendToHTML(" var y = String(b).toLowerCase(); \n");
      appendToHTML(" if (x > y) return 1; \n");
      appendToHTML(" if (x < y) return -1; \n");
      appendToHTML(" return 0; \n");
      appendToHTML("}\n");
      //Bug Id 78998, end

      appendToHTML("function populateSelectReceivers() {\n");
      appendToHTML(" tmp = document.form.col_1.options[document.form.col_1.length - 1].text;\n");
      appendToHTML(" for (; 0<document.form.col_1.length;) {\n");
      appendToHTML("    document.form.col_1.options[document.form.col_1.length-1] = null;\n");
      appendToHTML(" }\n");
      appendToHTML(" groupList = document.form.ITEM2_GROUP_DESC_1.value;\n");
      appendToHTML(" groupArray = groupList.split(String.fromCharCode(31));\n");
      appendToHTML(" groupArray = groupArray.slice(0,groupArray.length-1);  // last element is blank, created by the final ASCII 31.\n");
      appendToHTML(" groupArray.sort();\n");
      appendToHTML(" for (i=0; i<groupArray.length; i++) {\n");
      appendToHTML("    selectedGroup = groupArray[i];\n");
      appendToHTML("    groupDesc = selectedGroup.substr(0,selectedGroup.lastIndexOf('<'));\n");
      appendToHTML("    groupId = selectedGroup.substr(selectedGroup.lastIndexOf('<')+1,selectedGroup.length-selectedGroup.lastIndexOf('<')-2);\n");
      appendToHTML("    document.form.col_1.options[document.form.col_1.length] = new Option(groupDesc,\"<GRP>\"+groupId);\n");
      appendToHTML(" }\n");
      appendToHTML(" personList = document.form.ITEM2_PERSON_LIST_1.value;\n");
      appendToHTML(" personArray = personList.split(String.fromCharCode(31));\n");
      appendToHTML(" personArray = personArray.slice(0,personArray.length-1);  // last element is blank, created by the final ASCII 31.\n");
      appendToHTML(" personArray.sort();\n");
      appendToHTML(" for (i=0; i<personArray.length; i++) {\n");
      appendToHTML("    selectedUser = personArray[i];\n");
      appendToHTML("    userDesc = selectedUser;\n ");//Bug Id 55626;
      appendToHTML("    userId = selectedUser.substr(selectedUser.lastIndexOf('<')+1,selectedUser.length-selectedUser.lastIndexOf('<')-2);\n");
      appendToHTML("    document.form.col_1.options[document.form.col_1.length] = new Option(userDesc,userId);\n");
      appendToHTML(" }\n");
      appendToHTML(" document.form.col_1.options[document.form.col_1.length] = new Option(tmp);\n");
      appendToHTML("}\n");

      appendToHTML("function populateSelectBox() {\n");
      appendToHTML(" tmp = document.form.col_0.options[document.form.col_0.length - 1].text;\n");
      appendToHTML(" for (; 0<document.form.col_0.length;) {\n");
      appendToHTML("    document.form.col_0.options[document.form.col_0.length-1] = null;\n");
      appendToHTML(" }\n");
      //Bug Id 78998, start
      appendToHTML(" if ((document.form.GROUPS.checked == true) && (document.form.USERS.checked == true)){\n");
      appendToHTML("    populateGroupsAndUsers(document.form.ITEM2_SELECTED_CHARACTER.value);\n");
      appendToHTML(" }\n");
      appendToHTML(" else if (document.form.GROUPS.checked == true) {\n");
      appendToHTML("    populateGroups(document.form.ITEM2_SELECTED_CHARACTER.value);\n");//Bug Id 75677
      appendToHTML(" }\n");
      appendToHTML(" else if (document.form.USERS.checked == true) {\n");
      appendToHTML("    populateUsers(document.form.ITEM2_SELECTED_CHARACTER.value);\n");//Bug Id 75677
      appendToHTML(" }\n");
      //Bug Id 78998, end
      appendToHTML(" document.form.col_0.options[document.form.col_0.length] = new Option(tmp);\n");
      appendToHTML("}\n");
      
      //Bug Id 75677, Start
      appendToHTML("function populateByCharacter (selected_character) {\n");
      appendToHTML("   if (document.form.ITEM2_SELECTED_CHARACTERS.value.indexOf(selected_character+ String.fromCharCode(31))==-1) {\n");
      appendToHTML("      var ret = __connect(\n"); //tested
      appendToHTML("        '");
      appendToHTML(mgr.getURL());
      appendToHTML("?execute=populateUserGroups'\n");
      appendToHTML("        + '&SELECTED_CHAR=' + URLClientEncode(selected_character)\n");
      appendToHTML("        );\n");
      appendToHTML("      document.form.ITEM2_SELECTED_CHARACTERS.value += selected_character+ String.fromCharCode(31);\n");
      appendToHTML("    var personGroupArray;\n");
      appendToHTML("    var personArray;\n");
      appendToHTML("    var groupArray;\n");
      appendToHTML("    if (ret.length>0){ \n");
      appendToHTML("       ret = ret.substr(1,ret.length) \n");
      appendToHTML("       personGroupArray = ret.split(String.fromCharCode(29));\n");
      appendToHTML("       personArray = personGroupArray[0].split(String.fromCharCode(30));\n");
      appendToHTML("       groupArray = personGroupArray[1].split(String.fromCharCode(30));\n");
      appendToHTML("    }\n");
      appendToHTML("    if (personArray[0] != 'null'){ \n");
      appendToHTML("      document.form.ITEM2_PERSON_LIST_0.value +=  personArray[0];\n");
      appendToHTML("    }\n");
      appendToHTML("    if (personArray[1] != 'null') \n");
      appendToHTML("      document.form.ITEM2_IDENTITY_LIST_0.value += personArray[1];\n");
      appendToHTML("    if (groupArray[0] != 'null') \n");
      appendToHTML("      document.form.ITEM2_GROUP_DESC_0.value +=  groupArray[0];\n");
      appendToHTML("    if (groupArray[1] != 'null')\n");
      appendToHTML("      document.form.ITEM2_GROUP_ID_0.value += groupArray[1];\n");
      appendToHTML("   }\n");
      appendToHTML("   document.form.ITEM2_SELECTED_CHARACTER.value = selected_character;\n");
      appendToHTML("   populateSelectBox();\n");
      appendToHTML("}\n");
      //Bug Id 75677, End

      appendToHTML("function populateGroups(selected_character) {\n");//Bug Id 75677
      appendToHTML(" groupList = document.form.ITEM2_GROUP_DESC_0.value;\n");
      appendToHTML(" groupArray = groupList.split(String.fromCharCode(31));\n");
      appendToHTML(" groupArray = groupArray.slice(0,groupArray.length-1);  // last element is blank, created by the final ASCII 31.\n");
      appendToHTML(" groupArray.sort(ascendingOrder);\n");//Bug Id 78998
      appendToHTML(" for (i=0; i<groupArray.length; i++) {\n");
      appendToHTML("    selectedGroup = groupArray[i];\n");
      appendToHTML("    groupDesc = selectedGroup.substr(0,selectedGroup.lastIndexOf('<'));\n");
      appendToHTML("    if (groupDesc.substr(0,1).toUpperCase()== selected_character) {\n");//Bug Id 75677
      appendToHTML("       groupId = selectedGroup.substr(selectedGroup.lastIndexOf('<')+1,selectedGroup.length-selectedGroup.lastIndexOf('<')-2);\n");
      appendToHTML("       document.form.col_0.options[document.form.col_0.length] = new Option(groupDesc,\"<GRP>\"+groupId);\n");
      appendToHTML("    }\n");
      appendToHTML(" }\n");
      appendToHTML("}\n");

      appendToHTML("function populateUsers(selected_character) {\n");//Bug Id 75677
      appendToHTML(" personList = document.form.ITEM2_PERSON_LIST_0.value;\n");
      appendToHTML(" personArray = personList.split(String.fromCharCode(31));\n");
      appendToHTML(" personArray = personArray.slice(0,personArray.length-1);  // last element is blank, created by the final ASCII 31.\n");
      appendToHTML(" personArray.sort(ascendingOrder);\n");//Bug Id 78998
      appendToHTML(" for (i=0; i<personArray.length; i++) {\n");
      appendToHTML("    selectedUser = personArray[i];\n");
      appendToHTML("    userDesc = selectedUser;\n"); //Bug Id 55626
      appendToHTML("    if (userDesc.substr(0,1).toUpperCase()== selected_character) {\n");//Bug Id 75677
      appendToHTML("       userId = selectedUser.substr(selectedUser.lastIndexOf('<')+1,selectedUser.length-selectedUser.lastIndexOf('<')-2);\n");
      appendToHTML("       document.form.col_0.options[document.form.col_0.length] = new Option(userDesc,userId);\n");
      appendToHTML("    }\n");
      appendToHTML(" }\n");
      appendToHTML("}\n");
      
      //Bug Id 78998, start
      appendToHTML("function populateGroupsAndUsers(selected_character) {\n");
      appendToHTML(" var groupListOnly = document.form.ITEM2_GROUP_DESC_0.value;\n");
      appendToHTML(" var groupArrayOnly = groupListOnly.split(String.fromCharCode(31));\n");
      appendToHTML(" groupArrayOnly = groupArrayOnly.slice(0,groupArrayOnly.length-1);  // last element is blank, created by the final ASCII 31.\n");
      appendToHTML(" groupListOnly = '';\n");
      appendToHTML(" for (i=0; i < groupArrayOnly.length; i++) {\n");
      appendToHTML("    selectedGroup = groupArrayOnly[i] + String.fromCharCode(28);\n");
      appendToHTML("    groupListOnly = groupListOnly + String.fromCharCode(31) + selectedGroup;\n");
      appendToHTML(" }\n");
      appendToHTML(" var combinedUserGroupList = groupListOnly + String.fromCharCode(31) + document.form.ITEM2_PERSON_LIST_0.value;\n");
      appendToHTML(" combinedUserGroupListArray = combinedUserGroupList.split(String.fromCharCode(31));\n");
      appendToHTML(" combinedUserGroupListArray = combinedUserGroupListArray.slice(0,combinedUserGroupListArray.length-1);  // last element is blank, created by the final ASCII 31.\n");
      appendToHTML(" combinedUserGroupListArray.sort(ascendingOrder);\n");
      appendToHTML(" for (j=0; j<combinedUserGroupListArray.length; j++) {\n");
      appendToHTML("    if (combinedUserGroupListArray[j].indexOf(String.fromCharCode(28)) != -1 ){\n");
      appendToHTML("       selectedGroup = combinedUserGroupListArray[j];\n");
      appendToHTML("       groupDesc = selectedGroup.substr(0,selectedGroup.lastIndexOf('<'));\n");
      appendToHTML("       if (groupDesc.substr(0,1).toUpperCase()== selected_character) {\n");
      appendToHTML("          groupId = selectedGroup.substr(selectedGroup.lastIndexOf('<')+1,selectedGroup.length-selectedGroup.lastIndexOf('<')-3);\n");
      appendToHTML("          document.form.col_0.options[document.form.col_0.length] = new Option(groupDesc,\"<GRP>\"+groupId);\n");
      appendToHTML("       }\n");
      appendToHTML("    }\n");
      appendToHTML("    else {\n");
      appendToHTML("       selectedUser = combinedUserGroupListArray[j];\n");
      appendToHTML("       userDesc = selectedUser;\n"); 
      appendToHTML("       if (userDesc.substr(0,1).toUpperCase()== selected_character) {\n");
      appendToHTML("          userId = selectedUser.substr(selectedUser.lastIndexOf('<')+1,selectedUser.length-selectedUser.lastIndexOf('<')-2);\n");
      appendToHTML("          document.form.col_0.options[document.form.col_0.length] = new Option(userDesc,userId);\n");
      appendToHTML("       }\n");
      appendToHTML("    }\n");
      appendToHTML(" }\n");
      appendToHTML("}\n");
      //Bug Id 78998, end

      //Bug Id 75677, Start
      appendToHTML("function add()\n");
      appendToHTML("{\n");
      appendToHTML(" var srcbox =  eval('document.form.col_0');\n");
      appendToHTML(" var dstbox =  eval('document.form.col_1');\n");
      appendToHTML(" var dstField =  eval('document.form.ITEM2_PERSON_LIST_1');\n");
      appendToHTML(" var dstIdField =  eval('document.form.ITEM2_IDENTITY_LIST_1');\n");
      appendToHTML(" var dstGrpField =  eval('document.form.ITEM2_GROUP_DESC_1'); \n");
      appendToHTML(" var dstGrpIdField =  eval('document.form.ITEM2_GROUP_ID_1');\n");
      appendToHTML(" var indices = new Array();\n");
      appendToHTML("   for(j=0; srcbox.selectedIndex!=-1 && srcbox.selectedIndex!=srcbox.length-1; j++) {\n");
      appendToHTML("    indices[j] = srcbox.selectedIndex;\n");
      appendToHTML("    srcbox.options[srcbox.selectedIndex].selected = false;\n");
      appendToHTML("   }\n");
      appendToHTML("   for(i=0; i<indices.length; i++) {\n");
      appendToHTML("    var tmp = dstbox.options[dstbox.length-1].text;\n");
      appendToHTML("    var selectionText = srcbox.options[indices[i]].text;\n");
      appendToHTML("    var selectionValue = srcbox.options[indices[i]].value;\n");
      appendToHTML("    var selectionValueGroup = selectionValue;\n");
      appendToHTML("    var addOption = false;\n");
      appendToHTML("    if (selectionValue.search(/<GRP>/)==0) {\n");
      appendToHTML("       selectionValueGroup = selectionValueGroup.replace(/<GRP>/,'');\n");
      appendToHTML("       var selection = selectionText + \"<\" + selectionValueGroup + \">\";\n");
      appendToHTML("       if (dstGrpField.value.indexOf(selection) == -1 &&  dstGrpIdField.value.indexOf(selectionValueGroup) == -1) {\n");
      appendToHTML("          dstGrpField.value += selection + String.fromCharCode(31);\n");
      appendToHTML("          dstGrpIdField.value += selectionValueGroup + String.fromCharCode(31);\n");
      appendToHTML("          addOption = true;\n");
      appendToHTML("       }\n");
      appendToHTML("    }\n");
      appendToHTML("    else {\n");
      appendToHTML("       var selection = selectionText;\n");
      appendToHTML("       if (dstField.value.indexOf(selection) == -1 &&  dstIdField.value.indexOf(selectionValue) == -1) {\n");
      appendToHTML("          dstField.value += selection + String.fromCharCode(31);\n");
      appendToHTML("          dstIdField.value += selectionValue + String.fromCharCode(31);\n");
      appendToHTML("          addOption = true;\n");
      appendToHTML("       }\n");
      appendToHTML("    }\n");
      appendToHTML("    if (addOption) {\n");
      appendToHTML("       dstbox.options[dstbox.length-1].text  = selectionText;\n");
      appendToHTML("       dstbox.options[dstbox.length-1].value  = selectionValue;\n");
      appendToHTML("       dstbox.options[dstbox.length] = new Option(tmp);\n");
      appendToHTML("      }\n");
      appendToHTML("   }\n");
      appendToHTML("}\n");

      appendToHTML("function remove()\n");
      appendToHTML("{\n");
      appendToHTML(" var dstbox =  eval('document.form.col_1');\n");
      appendToHTML(" var dstField =  eval('document.form.ITEM2_PERSON_LIST_1');\n");
      appendToHTML(" var dstIdField =  eval('document.form.ITEM2_IDENTITY_LIST_1');\n");
      appendToHTML(" var dstGrpField =  eval('document.form.ITEM2_GROUP_DESC_1'); \n");
      appendToHTML(" var dstGrpIdField =  eval('document.form.ITEM2_GROUP_ID_1');\n");
      appendToHTML(" var indices = new Array();\n");
      appendToHTML("   for(j=0; dstbox.selectedIndex!=-1 && dstbox.selectedIndex!=dstbox.length-1; j++) {\n");
      appendToHTML("    indices[j] = dstbox.selectedIndex;\n");
      appendToHTML("    dstbox.options[dstbox.selectedIndex].selected = false;\n");
      appendToHTML("   }\n");
      appendToHTML("  for(i=0; i< indices.length; i++) {\n");
      appendToHTML("    var selectionText = dstbox.options[indices[i]].text;\n");
      appendToHTML("    var selectionValue = dstbox.options[indices[i]].value;\n");
      appendToHTML("    if (selectionValue.search(/<GRP>/)==0) {\n");
      appendToHTML("       selectionValue = selectionValue.replace(/<GRP>/,'');\n");
      appendToHTML("       var selection = selectionText + \"<\" + selectionValue + \">\";\n");
      appendToHTML("       dstGrpField.value = dstGrpField.value.substr(0,dstGrpField.value.indexOf(selection)) + dstGrpField.value.substr(dstGrpField.value.indexOf(selection)+selection.length+1);\n");
      appendToHTML("       dstGrpIdField.value = dstGrpIdField.value.substr(0,dstGrpIdField.value.indexOf(selectionValue)) + dstGrpIdField.value.substr(dstGrpIdField.value.indexOf(selectionValue)+selectionValue.length+1);\n");
      appendToHTML("    }\n");
      appendToHTML("    else {\n");
      appendToHTML("       var selection = selectionText;\n");
      appendToHTML("       dstField.value = dstField.value.substr(0,dstField.value.indexOf(selection)) + dstField.value.substr(dstField.value.indexOf(selection)+selection.length+1);\n");
      appendToHTML("       dstIdField.value = dstIdField.value.substr(0,dstIdField.value.indexOf(selectionValue)) + dstIdField.value.substr(dstIdField.value.indexOf(selectionValue)+selectionValue.length+1);\n");
      appendToHTML("    }\n");
      appendToHTML("   }\n");
      appendToHTML("   for(i=indices.length-1; i>=0; i--) {\n");
      appendToHTML("    dstbox.options[indices[i]] = null;\n");
      appendToHTML("   }\n");
      appendToHTML("}\n");
      //Bug Id 75677, End
      appendToHTML("</script>\n");
   }

}
