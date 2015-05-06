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
*  File        : NewSheetWizard.java
*  Modified    :
* 2002-11-11  Bakalk  Created.
* 2002-11-12  Prsalk	 Modified.
* 2002-12-03  Prsalk	 Fixed call id 92089
* 2002-12-18  Nisilk	 Fixed call id 92126
* 2003-03-19  Nisilk	 Modified  finish() to support XEDM funtionality.
* 2003-08-29  InoSlk  Call ID 101731: Modified doReset() and clone().
* 2003-09-04  NiSilk  Fixed call 102390.
* 2003-10-08  Bakalk  Call Id 106527: Save record set of docissue in ctx since have to pass them back.
* 2003-10-13  Bakalk  Call Id 106955: many modifications done. Remove finishOvw since not called anywhere now.
* 2003-10-17  Bakalk  Call Id 103691: modifications done in layout in order to make enogh room for latest image.
* 2003-10-17  InoSlk  Call ID 108331: Modified method finish().
* 2003-10-21  InoSlk  Call ID 108749: Modified finish() to pass the correct values for revisions when copying sheet data.
*                                     Added variable rev_note to hold revision text when step 2 is used.
* 2003-11-06  Bakalk  Call ID 110303: "Next" button was not there after getting back from second step, and fixed it.
* 2004-03-24  Dikalk  SP1 Merge. Bug ID 43189: Modified the SQL statement which fetches the doc sheet list
*                     in prepareDialog().
* 2004-06-28  Dikalk  Merged Bug Id 44558
* 2004-07-23  Dikalk  Fixed call 116048. Modified method finishDlg1()
* 2004-11-15  Bakalk  Merged Bug Id 43189
* 2005-08-02  SHTHLK  Merged bug Id 52298, Replaced mgr.translate with mgr.translateJavaScript in script functions.
* 2005-10-31  Amnalk  Merged Bug Id 53112.
* 2005-11-09  Amnalk  Fixed Call 128624.
* 2006-01-25  MDAHSE  Added throws FndException to preDefine().
* 2006-03-30  NEKOLK  Bug Id 56839: Renamed New Revision Note to Revision Text and New Sheet Order to Sheet Order.
* 2006-07-26  BAKALK  Bug ID 58216, Fixed Sql Injection.
* 2007-06-05  BAKALK  Call Id: 144476; Did many changes.
* 2007-08-15  ASSALK  Merged Bug 58526, Modified getContents().
* 2007-11-20  AMNALK  Bug Id 67230, Modified printContents() to limit max length of New Title Revision. 
* 2007-11-21  AMNALK  Bug Id 67456, Modified makeWizardHeader().
* 2007-11-30  AMNALK  Bug Id 65997, Removed the getCopy() call from the finish().
* 2008-04-03  SHTHLK  Bug Id 71615, Modified okFindITEM2(),finish() and preDefine() to get all the objects in all revisions for a particular sheet.
* 2008-06-02  SHTHLK  Bug Id 72326, Encoded the value set for SELECTED_VALUE
* 2008-06-08  SHTHLK  Bug Id 72326, Encoded the value set for New Sheet Number (new_sheet)
* 2008-07-15  AMNALK  Bug Id 69329, Added new functions GetNumberOfObjConnections() & storeRadioButtons(). Modified many places to add new step as the step 2
*				    when the number of object connections exceed 100
* 2008-08-08  AMNALK  Bug Id 74957, Select all the checkboxes when loading the second step of the new sheet wizard. 
* 2010-07-23  AMNALK  Bug Id 92069, Call javascript function selectUnselectAll() when loading the second step only if there are records.
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class NewSheetWizard extends DocSrv
{

   //===============================================================
   // Static constants
   //===============================================================

   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.NewSheetWizard");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
   private ASPLog log;
   private ASPBlock blk;
   private ASPRowSet headset;
   private ASPTable tbl;
   private ASPBlockLayout lay;
   private ASPBlock item2blk;
   private ASPRowSet item2set;
   private ASPTable item2tbl;
   private ASPBlockLayout item2lay;
   private ASPCommandBar bar;
   private ASPCommandBar item2bar;
   //Bug Id 69329, start
   private ASPBlock item3blk;
   private ASPRowSet item3set;
   private ASPTable item3tbl;
   private ASPBlockLayout item3lay;
   private ASPCommandBar item3bar;
   //Bug Id 69329, end

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private ASPBuffer rev_select_buff;
   private ASPBuffer rev_select_buff1;
   private ASPBuffer buff;
   private ASPBuffer docissue_buff;
   private ASPQuery q;
   private ASPCommand cmd;

   private boolean showDetail;
   private boolean start;
   private boolean newtitlerev_checkable;
   private boolean newtitlerev_val;
   private boolean structure_checkable;
   private boolean docsurvey_checkable;
   private boolean file_checkable;
   private boolean access_checkable;
   private boolean structure_val;
   private boolean copyapp_val;
   private boolean file_val;
   private boolean access_val;
   private boolean copy_val;
   private boolean revision_val;
   private boolean state_val;
   private boolean rec_val;
   private boolean activeFrame0;
   private boolean activeFrame1;
   private boolean activeFrame2; //Bug Id 69329
   private boolean multiTransfer;
   private boolean fromDocIssue;
   private boolean bPrevious; //Bug Id 74957

   private String url;
   private String c_url;
   private String callFrom;
   private String sApplicationSettings;
   private String doc_class;
   private String dclass;
   private String doc_no;
   private String dno;
   private String doc_sheet;
   private String dsheet;
   private String doc_rev;
   private String drev;
   private String revisions;
   private String rev;
   private String new_sheet;
   //private String nsheet;
   private String calcsheet;
   private String new_sheet_order;
   private String selected_value;
   private String nextSheetSelected;
   private String d_sheet;
   private String exist_sheet;
   private String exist_sheet2;
   private String title;
   private String revision;
   //private String n_sheet;
   private String s_order;
   private String redirectFrom;
   private String wizHeader;
   private String rev_note;
   private String sRadioButton; //Bug Id 69329

   private int z;
   private int noTransferRows;
   private int currentRow;

   private int [ ] selectDefaultArr;

   //===============================================================
   // Construction
   //===============================================================
   public NewSheetWizard(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }

  

   public void run()
   {
      ASPManager mgr = getASPManager();
      debug("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% nextSheetSelected = "+nextSheetSelected);

      fmt   = mgr.newASPHTMLFormatter();
      trans = mgr.newASPTransactionBuffer();
      ctx   = mgr.getASPContext();
      docissue_buff= ctx.readBuffer("DOCISSUES");
      log=mgr.getASPLog();

      showDetail = ctx.readFlag("SHOWDETAIL",showDetail);

      url   = mgr.readValue("SEND_URL",mgr.getQueryStringValue("SEND_URL"));
      c_url     = ctx.readValue("C_URL");
      new_sheet = ctx.readValue("NEW_SHEET");
      title     = ctx.readValue("TITLE");
      revision  = ctx.readValue("REVISION");
      s_order   = ctx.readValue("S_ORDER");
      wizHeader = ctx.readValue("WIZHEADER");
      nextSheetSelected = ctx.readValue("NEXTSHEETSELECTED");
      
      sRadioButton = ctx.readValue("RADIOBUTTON","COPYALL"); //Bug Id 69329

      if (mgr.isEmpty(url))
         url = c_url;
      else
         c_url = url;

      if (!mgr.isEmpty(c_url))
      {
         callFrom = (c_url.substring(c_url.lastIndexOf("/")+1)).toUpperCase() ;
         callFrom = (callFrom.substring(0,callFrom.lastIndexOf(".")));
      }

      if (!mgr.isEmpty(mgr.getQueryStringValue("SEND_URL")))
      {

         if (mgr.dataTransfered())
         {
            docissue_buff = mgr.getTransferedData();
            if (this.docissue_buff != null)
            {
               this.docissue_buff.traceBuffer("in send url *************");
            }
         }

         fromDocIssue = true;
      }
      showDetail = true;
     /* else
      {
         showDetail = false;
      }*/


      sApplicationSettings="";

      doc_class = mgr.readValue("DOC_CLASS",mgr.getQueryStringValue("DOC_CLASS"));
      dclass    = ctx.readValue("DOCCLASS");

      if (mgr.isEmpty(doc_class))
         doc_class = dclass;
      else
         dclass = doc_class;

      doc_no    = mgr.readValue("DOC_NO",mgr.getQueryStringValue("DOC_NO"));
      dno       = ctx.readValue("DOCNO");

      if (mgr.isEmpty(doc_no))
         doc_no = dno;
      else
         dno = doc_no;

      doc_sheet   = mgr.readValue("DOC_SHEET",mgr.getQueryStringValue("DOC_SHEET"));
      dsheet      = ctx.readValue("DSHEET");

      if (mgr.isEmpty(doc_sheet))
         doc_sheet = dsheet;
      else
         dsheet = doc_sheet;

      doc_rev   = mgr.readValue("DOC_REV",mgr.getQueryStringValue("DOC_REV"));
      drev      = ctx.readValue("DREV");

      if (mgr.isEmpty(doc_rev))
         doc_rev = drev;
      else
         drev = doc_rev;

      revisions   = mgr.readValue("REVISION",mgr.getQueryStringValue("REVISION"));
      rev      = ctx.readValue("REV");

      if (mgr.isEmpty(revisions))
         revisions = rev;
      else
         rev = revisions;

      //new_sheet   = mgr.readValue("DOC_SHEET",mgr.getQueryStringValue("DOC_SHEET"));
      //nsheet      = ctx.readValue("NSHEET");

      /*if (mgr.isEmpty(new_sheet))
         new_sheet = nsheet;
      else
         nsheet = new_sheet;*/

      new_sheet_order = ctx.readValue("NORDER");
      rev_note        = ctx.readValue("REV_NOTE");

      start = ctx.readFlag("START",false);


      // for the select box
      rev_select_buff  = ctx.readBuffer("REV_SELECT_BUFF");
      selected_value   = mgr.readValue("SELECTED_VALUE",mgr.getQueryStringValue("DOC_SHEET"));
      rev_select_buff1 = ctx.readBuffer("REV_SELECT_BUFF1");
      if (!mgr.isEmpty(mgr.readValue("NEXT_SHEET_NO",""))) {
         nextSheetSelected  = mgr.readValue("NEXT_SHEET_NO","");
      }
      
      d_sheet          = fmt.populateListBox(rev_select_buff, selected_value);

      // ctx vars used for checkboxes
      newtitlerev_checkable                = ctx.readFlag("NEWTITLEREV_CHECKABLE",false);
      newtitlerev_val      = ctx.readFlag("NEWTITLEREV_VAL",false);

      // ctx vars used for checkboxes
      structure_checkable  = ctx.readFlag("STRUCT_CHECKABLE",false);
      docsurvey_checkable  = ctx.readFlag("SURVEY_CHECKABLE",false);
      file_checkable       = ctx.readFlag("FILE_CHECKABLE",false);
      access_checkable     = ctx.readFlag("ACCESS_CHECKABLE",false);
      structure_val        = ctx.readFlag("STRUCT_VAL",false);
      copyapp_val          = ctx.readFlag("SURVEY_VAL",false);
      file_val             = ctx.readFlag("FIL_VAL",false);
      access_val           = ctx.readFlag("ACC_VAL",false);
      copy_val             = ctx.readFlag("COPY_VAL",false);
      revision_val         = ctx.readFlag("REV_VAL",true);
      state_val            = ctx.readFlag("STATE_VAL",true);
      rec_val              = ctx.readFlag("REC_VAL",true);

      //Frames
      activeFrame0 = ctx.readFlag("ACTIVEFRAME0",true);
      activeFrame1 = ctx.readFlag("ACTIVEFRAME1",false);
      activeFrame2 = ctx.readFlag("ACTIVEFRAME2",false);//Bug Id 69329

      buff           = ctx.readBuffer("BUFF");
      noTransferRows = ctx.readNumber("NOTRANSFERROWS",0);
      currentRow     = ctx.readNumber("CURRENTROW",0);
      multiTransfer  = ctx.readFlag("MULTITRANSFER",false);

      bPrevious = ctx.readFlag("B_PREVIOUS",false); //Bug Id 74957

      mgr.setPageExpiring();


      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if (!fromDocIssue && mgr.dataTransfered())
         runQuery();
      else if ((!mgr.isEmpty(mgr.getQueryStringValue("DOC_NO"))) && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS"))) && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_SHEET"))) &&(!mgr.isEmpty(mgr.getQueryStringValue("DOC_SHEET")))&&(!mgr.isEmpty(mgr.getQueryStringValue("DOC_REV"))) && (!mgr.isEmpty(mgr.getQueryStringValue("SEND_URL"))))
         prepareDialog();
      else if (mgr.buttonPressed("CANCEL"))
         Cancel();

      else if ("NEXT".equals(mgr.readValue("NEXT")))
      {
         Next();
      }
      else if (mgr.buttonPressed("PREVIOUS"))
         Previous();
      else if (mgr.buttonPressed("FINISH"))
         finish();

      else if ("FIN".equals(mgr.readValue("FIN")))
      {
         if (showDetail)
            finishInDlg1();
         else
            finish();
      }

      else if (!("".equals(mgr.readValue("SELECTED_VALUE"))))
      {
         assignFields();
      }

      if (mgr.isEmpty(wizHeader))
      {
         wizHeader = makeWizardHeader();
      }

      if (this.docissue_buff != null)
      {
         this.docissue_buff.traceBuffer("in run after doing all operations*************");
      }
      //==================general ctx variables====================================
      ctx.writeValue("DOCCLASS",dclass);
      ctx.writeValue("DOCNO",dno);
      ctx.writeValue("DSHEET",dsheet);
      ctx.writeValue("DREV",drev);
      ctx.writeValue("REV",rev);
      ctx.writeValue("SEND_URL",url);
      ctx.writeValue("C_URL",c_url);
      ctx.writeValue("CALLFROM",callFrom);
      ctx.writeValue("NEW_SHEET",new_sheet);
      ctx.writeValue("TITLE",title);
      ctx.writeValue("REVISION",revision);
      ctx.writeValue("S_ORDER",s_order);
      ctx.writeValue("WIZHEADER",wizHeader);
      ctx.writeValue("DOCNO",dno);
      ctx.writeValue("NEXTSHEETSELECTED",nextSheetSelected);
      ctx.writeFlag("FIL_VAL",file_val);

      //===================== for the select box==================================
      if (rev_select_buff.countItems()>0)
         ctx.writeBuffer("REV_SELECT_BUFF",rev_select_buff);
      if (rev_select_buff1.countItems()>0)
         ctx.writeBuffer("REV_SELECT_BUFF1",rev_select_buff1);

      //===============================Frames=======================


      ctx.writeFlag("ACTIVEFRAME0",activeFrame0);
      ctx.writeFlag("ACTIVEFRAME1",activeFrame1);
      ctx.writeFlag("ACTIVEFRAME2",activeFrame2); //Bug Id 69329

      //==================variables===============================


      ctx.writeFlag("START",start);
      if (buff != null)
         ctx.writeBuffer("BUFF",buff);
      if (docissue_buff != null)
         ctx.writeBuffer("DOCISSUES",docissue_buff);
      ctx.writeNumber("NOTRANSFERROWS",noTransferRows);
      ctx.writeNumber("CURRENTROW",currentRow);
      ctx.writeFlag("MULTITRANSFER",multiTransfer);

      //============================== Write ctx vars used for checkboxes========
      ctx.writeFlag("STRUCT_CHECKABLE",structure_checkable);
      ctx.writeFlag("SURVEY_CHECKABLE",docsurvey_checkable);
      ctx.writeFlag("FILE_CHECKABLE",file_checkable);
      ctx.writeFlag("ACCESS_CHECKABLE",access_checkable);
      ctx.writeFlag("NEWTITLEREV_CHECKABLE",newtitlerev_checkable);
      ctx.writeFlag("STRUCT_VAL",structure_val);
      ctx.writeFlag("SURVEY_VAL",copyapp_val);
      ctx.writeFlag("FIL_VAL",file_val);
      ctx.writeFlag("ACC_VAL",access_val);
      ctx.writeFlag("COPY_VAL",copy_val);
      ctx.writeFlag("NEWTITLEREV_VAL",newtitlerev_val);
      ctx.writeFlag("REV_VAL",revision_val);
      ctx.writeFlag("STATE_VAL",state_val);
      ctx.writeFlag("REC_VAL",rec_val);

      ctx.writeValue("RADIOBUTTON",sRadioButton);//Bug Id 69329

      ctx.writeFlag("B_PREVIOUS",bPrevious); //Bug Id 74957


   }


   public void  okFindITEM2()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      item2set.clear();
      q = trans.addEmptyQuery(item2blk);
      //Bug ID 45944, inoslk, start
      //Bug Id 71615, Removed DOC_REV from the query
      q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ? AND DOC_SHEET = ?");
      q.addParameter("DOC_CLASS",dclass);
      q.addParameter("DOC_NO",dno);
      q.addParameter("DOC_SHEET",dsheet);
      q.includeMeta("ALL");

      mgr.submit(trans);

      if (item2set.countRows() == 0)
      {
         item2set.clear();
      }

   }


   public void  runQuery()
   {
      ASPManager mgr = getASPManager();

      buff = mgr.getTransferedData();
      noTransferRows=buff.countItems();
      multiTransfer=true;

      currentRow=0;

      ASPBuffer subbuff1 = buff.getBufferAt(currentRow);
      dclass = doc_class = subbuff1.getValueAt(0);
      dno = doc_no = subbuff1.getValueAt(1);
      selected_value = subbuff1.getValueAt(2);

      ASPBuffer subbuff2 = buff.getBufferAt(currentRow);
      dclass = doc_class = subbuff2.getValueAt(0);
      dno = doc_no = subbuff2.getValueAt(1);
      nextSheetSelected = subbuff2.getValueAt(2);

      prepareDialog();
      noTransferRows-=1;
   }


   public void  getNextRowFromBuff()
   {
      currentRow += 1;

      ASPBuffer subbuff1 = buff.getBufferAt(currentRow);
      dclass = doc_class = subbuff1.getValueAt(0);
      dno    = doc_no    = subbuff1.getValueAt(1);
      selected_value = subbuff1.getValueAt(2);

      ASPBuffer subbuff2 = buff.getBufferAt(currentRow);
      dclass=doc_class = subbuff2.getValueAt(0);
      dno=doc_no = subbuff2.getValueAt(1);
      nextSheetSelected = subbuff2.getValueAt(2);

      headset.clear();
      item2set.clear();
      prepareDialog();

      noTransferRows-=1;

      activeFrame0 = true;
      activeFrame1 = false;
      activeFrame2 = false;//Bug Id 69329
   }

   //=============================================================================
   //   Validation
   //=============================================================================

   public void  validate()
   {
      ASPManager mgr = getASPManager();
      String val = mgr.readValue("VALIDATE");
      mgr.showError("VALIDATE not implemented");
      mgr.endResponse();
   }

   //=============================================================================

   public void  prepareDialog()
   {
      ASPManager mgr = getASPManager();

      // GET VALUES FOR COMBO BOX

      url = mgr.getQueryStringValue("SEND_URL");
      trans.clear();
      //trans.addQuery("EXISTING_REVS","DOC_ISSUE","DOC_REV,DOC_REV PR2","DOC_NO='"+doc_no+"' AND DOC_CLASS='"+doc_class+"'","DOC_REV");
      //Bug ID 43189, inoslk, modified the sql string which fetches the doc sheet list in the following queries
      //Bug ID 45944, inoslk, start

       //bug 58216 starts
       ASPQuery q =  trans.addQuery("EXISTING_SHEETS","SELECT DISTINCT DOC_SHEET,DOC_SHEET PR2 FROM DOC_ISSUE WHERE DOC_NO = ? AND DOC_CLASS = ?");
       q.addParameter("DOC_NO",doc_no);
       q.addParameter("DOC_CLASS",doc_class);

       q = trans.addQuery("EXISTING_SHEETS2","SELECT DISTINCT DOC_SHEET,DOC_SHEET PR2 FROM DOC_ISSUE WHERE DOC_NO = ? AND DOC_CLASS = ?");
       q.addParameter("DOC_NO",doc_no);
       q.addParameter("DOC_CLASS",doc_class);
       //bug 58216 end



      mgr.submit(trans);


      rev_select_buff = ctx.getDbState().getBuffer("EXISTING_SHEETS");
      ctx.writeBuffer("REV_SELECT_BUFF",rev_select_buff);
      exist_sheet = fmt.populateListBox(rev_select_buff,selected_value);

      rev_select_buff1 = ctx.getDbState().getBuffer("EXISTING_SHEETS2");
      ctx.writeBuffer("REV_SELECT_BUFF1",rev_select_buff1);
      exist_sheet2 = fmt.populateListBox(rev_select_buff1,nextSheetSelected);

      okFindITEM2();

      assignFields();
   }

   //=============================================================================
   //  CHANGE TITLE REVISION
   //=============================================================================

   public void  assignTitleRev()
   {
      ASPManager mgr = getASPManager();

      String newTitleRev = mgr.readValue("NEWREV");
      if (!(mgr.isEmpty(newTitleRev)))
      {
         trans.clear();
         cmd = trans.addCustomCommand("CHANGETITLEREV","DOC_TITLE_API.New_Title_Revision");
         cmd.addParameter("DOC_CLASS",doc_class);
         cmd.addParameter("DOC_NO",doc_no);
         cmd.addParameter("REVISION",newTitleRev);
         cmd.addParameter("INFO",mgr.readValue("NEWREVNOTE"));
         trans = mgr.perform(trans);
         trans.clear();
      }

   }


   public void  assignFields()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomCommand("GETINFO","DOC_ISSUE_API.Get_Info");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("DOC_SHEET",selected_value);
      cmd.addParameter("DOC_REV",doc_rev);
      cmd.addParameter("HAS_STRUCTURE");
      cmd.addParameter("HAS_APP_ROUTE");
      cmd.addParameter("HAS_FILE");
      trans = mgr.perform(trans);
      String has_structure = trans.getValue("GETINFO/DATA/HAS_STRUCTURE");
      String has_app_route = trans.getValue("GETINFO/DATA/HAS_APP_ROUTE");
      String has_file = trans.getValue("GETINFO/DATA/HAS_FILE");

      if ("1".equals(has_structure))
         structure_val=structure_checkable=true ;
      else
         structure_val=structure_checkable=false;

      if ("1".equals(has_app_route))
         copyapp_val=docsurvey_checkable=true;
      else
         copyapp_val=docsurvey_checkable=false;

      if ("1".equals(has_file))
         file_val=file_checkable=true;
      else
         file_val=file_checkable=false;

      trans.clear();
      cmd = trans.addCustomFunction("EXISTCHECK","DOC_REFERENCE_OBJECT_API.Exist_Doc_Reference","DOC_REF_EXIST");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("DOC_SHEET",selected_value);
      cmd.addParameter("DOC_REV",doc_rev);
      trans = mgr.perform(trans);
      String has_access = trans.getValue("EXISTCHECK/DATA/DOC_REF_EXIST");

      access_val=access_checkable=true;
      newtitlerev_val=newtitlerev_checkable=false;

      trans.clear();

      ASPBuffer data = trans.getBuffer("ITEM1/DATA");
      headset.addRow(data);

      ASPBuffer r = headset.getRow();
      r.setFieldItem("HAS_STRUCTURE",structure_val+"");
      headset.setRow(r);

      ASPBuffer g = headset.getRow();
      g.setFieldItem("HAS_APP_ROUTE",copyapp_val+"");
      headset.setRow(g);

      ASPBuffer b = headset.getRow();
      b.setFieldItem("HAS_FILE",file_val+"");
      headset.setRow(b);

      ASPBuffer c = headset.getRow();
      c.setFieldItem("DOC_REF_EXIST",access_val+"");
      headset.setRow(c);
   }

   //=============================================================================
   //   PUSH-BUTTON FUNCTIONS
   //=============================================================================

   public String  makeWizardHeader()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomFunction("GETDOCTITLE","DOC_TITLE_API.Get_Title","DUMMY1");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);

      cmd = trans.addCustomFunction("GETREVISION","DOC_TITLE_API.Get_Revision","REVISION");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);

      cmd = trans.addCustomFunction("GETSHEET","DOC_ISSUE_API.Calc_New_Doc_Sheet","DOC_SHEET");
      cmd.addParameter("DOC_SHEET",selected_value);

      cmd = trans.addCustomFunction("GETSHEETORDER","DOC_ISSUE_API.Get_Max_Sheet_Order","SHEET_ORDER");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);

      trans = mgr.perform(trans);

      title = trans.getValue("GETDOCTITLE/DATA/DUMMY1");
      revision = trans.getValue("GETREVISION/DATA/REVISION");
      new_sheet = trans.getValue("GETSHEET/DATA/DOC_SHEET");
      s_order = trans.getValue("GETSHEETORDER/DATA/SHEET_ORDER");
      trans.clear();

      //If the title revision is null then no text should be displayed.
      //Bug Id 67456, start
      if (revision == null) 
      {
	  revision = "";
      }
      //Bug Id 67456

      if (mgr.isEmpty(calcsheet))
      {
         calcsheet = new_sheet;// check again
      }
      ctx.writeValue("CALCSHEET",calcsheet);

      return title + "<font size=1> ("  + doc_class + " - " + doc_no + " - "+ doc_sheet +" - " +doc_rev + ")</font>";

   }


   public String  getAppSettings()
   {
      ASPManager mgr = getASPManager();
      String sUser,sAppSet;

      sUser   = mgr.getUserId().toUpperCase();
      sUser   = sUser.replace('\\','~');
      sAppSet  = "application=DOCMAW^instance=";
      sAppSet += mgr.getConfigParameter("APPLICATION/ID")+"^language=";
      sAppSet += mgr.getConfigParameter("APPLICATION/LANGUAGE")+"^username="+sUser+"^";
      return sAppSet;
   }


   public void  assignVal()
   {
      ASPManager mgr = getASPManager();

      if ("ON".equals(mgr.readValue("CBCOPYSTRUCTURE")))
         structure_val=true;
      else
         structure_val=false;

      if ("ON".equals(mgr.readValue("CBCOPYDOCUMENTSURVEY")))
         copyapp_val=true;
      else
         copyapp_val=false;

      if ("ON".equals(mgr.readValue("CBCOPYFILE")))
         file_val=true;
      else
         file_val=false;

      if ("ON".equals(mgr.readValue("CBCOPYACCESS")))
         access_val=true;
      else
         access_val=false;

      if ("ON".equals(mgr.readValue("CBCOPYSHEET")))
         copy_val=true;
      else
         copy_val=false;

      if ("ON".equals(mgr.readValue("CBSETREV")))
         revision_val=true;
      else
         revision_val=false;

      if ("ON".equals(mgr.readValue("CBSETSTATE")))
         state_val=true;
      else
         state_val=false;

      if ("ON".equals(mgr.readValue("CBCOPYREC")))
         rec_val=true;
      else
         rec_val=false;


      ctx.writeFlag("STRUCT_CHECKABLE",structure_checkable);
      ctx.writeFlag("SURVEY_CHECKABLE",docsurvey_checkable);
      ctx.writeFlag("FILE_CHECKABLE",file_checkable);
      ctx.writeFlag("ACCESS_CHECKABLE",access_checkable);
      ctx.writeFlag("NEWTITLEREV_CHECKABLE",newtitlerev_checkable);
      ctx.writeFlag("STRUCT_VAL",structure_val);
      ctx.writeFlag("SURVEY_VAL",copyapp_val);
      ctx.writeFlag("FIL_VAL",file_val);
      ctx.writeFlag("ACC_VAL",access_val);
      ctx.writeFlag("COPY_VAL",copy_val);
      ctx.writeFlag("NEWTITLEREV_VAL",newtitlerev_val);
      ctx.writeFlag("REV_VAL",revision_val);
      ctx.writeFlag("STATE_VAL",state_val);
      ctx.writeFlag("REC_VAL",rec_val);
   }


   public void  finish()
   {
      boolean seconStepNotAvailable = false;
      ASPManager mgr = getASPManager();

      //if the second step Not available OR we do not go there

      if ("FIN".equals(mgr.readValue("FIN")))
      {
         seconStepNotAvailable = true;
      }

      if (seconStepNotAvailable)
      {
         this.Next();//we must colloect the information on the first step: bakalk
      }

      int numRows,x,hasst,hasap,hasfl,hasac,updateAllow,setRev,setState,copyrec;
      String d_rev; //for rev for new sheet

      if (structure_val)
         hasst=1;
      else
         hasst=0;
      if (copyapp_val)
         hasap=1;
      else
         hasap=0;
      if (file_val)
         hasfl=1;
      else
         hasfl=0;
      if (access_val)
         hasac=1;
      else
         hasac=0;

      if (copy_val)
         updateAllow=1;
      else
         updateAllow=0;
      if (revision_val)
         setRev=1;
      else
         setRev=0;
      if (state_val)
         setState=1;
      else
         setState=0;
      if (rec_val)
         copyrec=1;
      else
         copyrec=0;
      //following seems to be unneccessary but did not remove: bakalk
      if ("FIN".equals(mgr.readValue("FIN")))
      {
         activeFrame0 = true;
         activeFrame1 = false;
      }
      assignTitleRev();
      String tmpFileLocation;

      if ((file_val) && (copy_val))
      {
         sApplicationSettings=getAppSettings();

      }

      if (mgr.isEmpty(new_sheet))
         new_sheet = calcsheet;
      else
         calcsheet = new_sheet;


      trans.clear();
      cmd = trans.addCustomCommand("NEWSHEET2","DOC_ISSUE_API.CREATE_NEW_SHEET__");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("DOC_SHEET", new_sheet);
      cmd.addParameter("DOC_REV",doc_rev);
      cmd.addParameter("PRE_SHEET", selected_value);
      cmd.addParameter("NEXT_DOC_SHEET",nextSheetSelected);
      cmd.addParameter("DOC_REV_TEXT",rev_note);
      cmd.addParameter("SHEET_ORDER",new_sheet_order);
      cmd.addParameter("COPY_METH",updateAllow+"");
      cmd.addParameter("SET_REV",setRev+"");
      cmd.addParameter("DUMMY3",copyrec+"");

      trans = mgr.perform(trans);


      d_rev = trans.getValue("NEWSHEET2/DATA/DOC_REV");//rev for new sheet

      trans.clear();

      //Bug Id 69329, start
      if (GetNumberOfObjConnections() > 100) 
      {
	  storeRadioButtons();
	  if (!"NOCOPY".equals(ctx.readValue("RADIOBUTTON"))) 
	  {
  	     trans.clear();
  	     cmd = trans.addCustomCommand("NEWREV3","DOC_REFERENCE_OBJECT_API.Move_All_Docref_To_Newsheet");
             cmd.addParameter("DOC_CLASS",doc_class);
             cmd.addParameter("DOC_NO",doc_no);
  	     cmd.addParameter("PRE_SHEET", selected_value);
             cmd.addParameter("DOC_SHEET", new_sheet);
	     cmd.addParameter("DOC_REV",d_rev);
            
  	     trans = mgr.perform(trans);

  	     trans.clear();
	  }
      }
      else
      {
      if (!seconStepNotAvailable)//if the second step available
      {
         item2set.selectRows();
         item2set.storeSelections();
         item2set.setFilterOn();

      }//otherwise we move all objects simply.
      numRows=item2set.countRows();

      item2set.first();


      for (x=0;x<numRows;x++)
      {
         cmd = trans.addCustomCommand("NEWREV3"+x,"DOC_REFERENCE_OBJECT_API.Move_Docref_To_Newsheet");
         cmd.addParameter("DOC_CLASS",doc_class);
         cmd.addParameter("DOC_NO",doc_no);
         cmd.addParameter("DOC_SHEET",doc_sheet);
         cmd.addParameter("OLD_PART_REV",item2set.getRow().getValue("DOC_REV") ); //Bug Id 71615
         cmd.addParameter("LU_NAME",item2set.getRow().getValue("LU_NAME") );
         cmd.addParameter("KEY_REF",item2set.getRow().getValue("KEY_REF"));
         cmd.addParameter("NEW_PART_SHEET",new_sheet);
         cmd.addParameter("INSTR_1",d_rev);


         item2set.next();
      }
      trans = mgr.perform(trans);
      trans.clear();

      if (!seconStepNotAvailable)
      {
         item2set.setFilterOff();
      }
      }
      //Bug Id 69329, end

      if (copy_val)
      {
         trans.clear();

         cmd = trans.addCustomCommand("COPYSHEET","DOC_ISSUE_API.COPY_SHEET");
         cmd.addParameter("DOC_CLASS",doc_class);
         cmd.addParameter("DOC_NO",doc_no);
         cmd.addParameter("DOC_SHEET",new_sheet);
         cmd.addParameter("DOC_REV",d_rev);
         cmd.addParameter("OLD_DOC_SHEET",selected_value);
         cmd.addParameter("INSTR_1",doc_rev);
         cmd.addParameter("HAS_STRUCTURE",hasst+"");
         cmd.addParameter("HAS_APP_ROUTE",hasap+"");
         cmd.addParameter("HAS_FILE",hasfl+"");
         cmd.addParameter("DOC_REF_EXIST",hasac+"");
         cmd.addParameter("NEW_REVISION","1");
         cmd.addParameter("SET_REV",setRev+"");
         cmd.addParameter("SET_STATE",setState+"");
         trans = mgr.perform(trans);
         trans.clear();

         if (file_val)
         {
            try
            {
               copyFileInRepository(doc_class, doc_no, selected_value, doc_rev, doc_class, doc_no, new_sheet,d_rev);
            }
            catch (Exception Err)
            {
               mgr.showError(mgr.translate("DOCMAWNEWSHEETWIZARDCREATENEWSHEETFAILED: Create New Sheet failed.\\n"));
            }
         }
      }

      int no_of_items = docissue_buff.countItems();

      String buffer_name = (no_of_items+1)+"";
      ASPBuffer new_row  = docissue_buff.addBufferAt(buffer_name,no_of_items);
      new_row.addItem("DOC_CLASS",doc_class);
      new_row.addItem("DOC_NO",doc_no);
      new_row.addItem("DOC_SHEET",new_sheet);
      new_row.addItem("DOC_REV",d_rev);

      String new_doc_key = doc_class +"^";
      new_doc_key += doc_no +"^";
      new_doc_key += new_sheet +"^";
      new_doc_key += d_rev +"^";

      mgr.transferDataTo(c_url+"?DOC_KEY="+mgr.URLEncode(new_doc_key),docissue_buff);
   }


   public void  selectDefaultRows()
   {

      item2set.first();
      for (z=0;z<selectDefaultArr.length;z++)
      {
         if (selectDefaultArr[z]==1)
            item2set.selectRow();

         item2set.next();
      }

   }


   public void  finishInDlg1()
   {
      ASPManager mgr = getASPManager();
      new_sheet = mgr.readValue("NEWSHEET");

      String exist = "";

      trans.clear();

      cmd = trans.addCustomFunction("SHEETEXISTS","Doc_Issue_Util_API.Check_Sheet_Exist_","OUTSTR_1");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("DOC_SHEET",new_sheet);

      trans = mgr.perform(trans);
      exist = trans.getValue("SHEETEXISTS/DATA/OUTSTR_1");

      if ("TRUE".equals(exist))
      {
         mgr.showAlert(mgr.translate("DOCMAWNEWSHEETWIZSHEETEXISTS: The document sheet already exists. Please enter another sheet number."));
      }
      else
      {
         assignVal();
         finish();
      }
   }


   public void  Next()
   {
      ASPManager mgr = getASPManager();
      new_sheet = mgr.readValue("NEWSHEET");
      assignVal();

      String exist = "";
      trans.clear();

      cmd = trans.addCustomFunction("SHEETEXISTS","Doc_Issue_Util_API.Check_Sheet_Exist_","OUTSTR_1");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("DOC_SHEET",new_sheet);

      trans = mgr.perform(trans);
      exist = trans.getValue("SHEETEXISTS/DATA/OUTSTR_1");

      if ("TRUE".equals(exist))
      {
         mgr.showAlert(mgr.translate("DOCMAWNEWSHEETWIZSHEETEXISTS: The document sheet already exists. Please enter another sheet number."));
      }
      else
      {
         start = true;
         activeFrame0 = false;
         //Bug Id 69329, start
	 if (GetNumberOfObjConnections() < 100)
	 {
         activeFrame1 = true;
	     activeFrame2 = false;
	 }
	 else
	 {
	     activeFrame1 = false;
	     activeFrame2 = true;
	 }
	 //Bug Id 69329, end

         calcsheet = ctx.readValue("CALCSHEET");

         new_sheet = mgr.readValue("NEWSHEET");

         new_sheet_order = mgr.readValue("NEWSORDER");
         ctx.writeValue("NORDER", new_sheet_order);
         ctx.writeValue("SELECTED_VALUE",selected_value);
         ctx.writeFlag("STRUCT_VAL",structure_val);
         ctx.writeFlag("SURVEY_VAL",copyapp_val);
         ctx.writeFlag("FIL_VAL",file_val);
         ctx.writeFlag("ACC_VAL",access_val);
         ctx.writeFlag("COPY_VAL",copy_val);
         ctx.writeFlag("NEWTITLEREV_VAL",newtitlerev_val);
         ctx.writeFlag("REV_VAL",revision_val);
         ctx.writeFlag("STATE_VAL",state_val);
         ctx.writeFlag("REC_VAL",rec_val);
         rev_note = mgr.readValue("NEWRNOTE");
         ctx.writeValue("REV_NOTE",rev_note);

         assignTitleRev();
      }
   }


   public void  getSelectDefaultRows()
   {
      int numRows;
      int x;
      String lock;
      String updateRevision;

      selectDefaultArr = null;

      numRows=item2set.countRows();


      item2set.first();


      for (x=0;x<numRows;x++)
      {
         lock=item2set.getRow().getValue("SURVEY_LOCKED_FLAG_DB");
         updateRevision=item2set.getRow().getValue("KEEP_LAST_DOC_REV_DB");

         if (( "0".equals(lock) ) &&  ( "L".equals(updateRevision) ))
            selectDefaultArr[x]=1;
         else
            selectDefaultArr[x]=0;

         item2set.next();
      }

   }


   public void  Previous()
   {
      activeFrame0 = true;
      activeFrame1 = false;
      activeFrame2 = false;//Bug Id 69329
      ASPBuffer row = headset.getRow();

      row.setValue("STRUCT_CHECKABLE",structure_checkable+"");
      row.setValue("SURVEY_CHECKABLE",docsurvey_checkable+"");
      row.setValue("FILE_CHECKABLE",file_checkable+"");
      row.setValue("ACCESS_CHECKABLE",access_checkable+"");

      headset.setRow(row);

      //Bug Id 69329, start
      if (GetNumberOfObjConnections() > 100) 
      {
	  storeRadioButtons();
      }
      else
      {
      item2set.storeSelections();
   }
      //Bug Id 69329, end
      
      bPrevious = true; //Bug Id 74957
   }


   public void  Cancel()
   {
      ASPManager mgr = getASPManager();

      if (multiTransfer)
      {
         redirectFrom = ctx.findGlobal("__REDIRECT_FROM","");
         mgr.transferDataTo(redirectFrom,buff);
      }
      else
         mgr.redirectTo(url+"?DOC_NO="+mgr.URLEncode(doc_no)+"&DOC_CLASS="+mgr.URLEncode(doc_class)+"&DOC_SHEET="+mgr.URLEncode(selected_value)+"&DOC_REV="+mgr.URLEncode(doc_rev));
   }

   //Bug Id 69329, start
   public int GetNumberOfObjConnections()
   {
       ASPManager mgr = getASPManager();

       trans.clear();
       cmd = trans.addCustomFunction("NOOFOBJECTCON","DOC_REFERENCE_OBJECT_API.Get_Num_Of_All_Connections", "ITEM3_NO_OF_OBJ_CON");
       cmd.addParameter("DOC_CLASS",   doc_class );
       cmd.addParameter("DOC_NO",   doc_no );
       cmd.addParameter("DOC_SHEET",   doc_sheet );

       trans = mgr.perform(trans);

       String sno_of_obj_con = trans.getValue("NOOFOBJECTCON/DATA/ITEM3_NO_OF_OBJ_CON");
       
       trans.clear();

       return Integer.parseInt(sno_of_obj_con);

   }
   
   public void storeRadioButtons()
   {
       ASPManager mgr = getASPManager();
       String buttonCopyAll = mgr.readValue("COPYALL");
       String buttonNoCopy = mgr.readValue("NOCOPY");

       if ("COPYALL".equals(buttonCopyAll)) 
       {
	   ctx.writeValue("RADIOBUTTON", buttonCopyAll);
	   sRadioButton = buttonCopyAll;
       }
       else if ("NOCOPY".equals(buttonNoCopy))
       {
	   ctx.writeValue("RADIOBUTTON", buttonNoCopy);
	   sRadioButton = buttonNoCopy;
       }

   }
   //Bug Id 69329, end

   //============================================================================
   //   CUSTOM FUNCTIONS
   //============================================================================

   public void  doselect()
   {

   }


   public void  preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();


      disableConfiguration();

      disableHeader();

      // *********************************** DLG 1 ***********************************
      blk = mgr.newASPBlock("ITEM1");

      blk.addField("OBJID").
      setHidden();

      blk.addField("OBJVERSION").
      setHidden();

      blk.addField( "DOC_CLASS" ).
      setHidden().
      setFunction("''");

      blk.addField( "DOC_NO" ).
      setHidden().
      setFunction("''");

      blk.addField("DOC_REV").
      setSize(7).
      setMandatory().
      setLabel("DOCMAWNEWSHEETWIZARDSNEWPARTREV: Revision").
      setUpperCase().
      setHidden().
      setFunction("''");

      blk.addField("DOC_SHEET").
      setSize(7).
      setMandatory().
      setLabel("DOCMAWNEWSHEETWIZARDSNEWPARTSHEET: Sheet No").
      setUpperCase().
      setHidden();

      blk.addField("REVISION").
      setSize(20).
      setLabel("DOCMAWNEWSHEETWIZARDTITLEREVISION: Existing Title Revision").
      setUpperCase().
      setHidden().
      setFunction("''");

      blk.addField( "INFO" ).
      setHidden().
      setFunction("''");



      blk.addField( "OLD_DOC_CLASS" ).
      setHidden().
      setFunction("''");

      blk.addField( "OLD_DOC_NO" ).
      setHidden().
      setFunction("''");

      blk.addField("PRE_SHEET").
      setHidden().
      setFunction("''");

      blk.addField("NEXT_DOC_SHEET").
      setHidden().
      setFunction("''").
      setUpperCase();    //Bug Id 77083


      blk.addField("SHEET_ORDER").
      setSize(10).
      setHidden();

      blk.addField( "OLD_DOC_SHEET" ).
      setHidden().
      setFunction("''");


      blk.addField("NO_OF_SHEETS").
      setSize(7).
      setMandatory().
      setUpperCase().
      setHidden();

      blk.addField( "HAS_STRUCTURE" ).
      setHidden().
      setFunction("0");


      blk.addField( "HAS_APP_ROUTE" ).
      setHidden().
      setFunction("0");


      blk.addField( "SET_REV" ).
      setHidden().
      setFunction("0");


      blk.addField( "SET_STATE" ).
      setHidden().
      setFunction("0");

      blk.addField( "COPY_METH" ).
      setHidden().
      setFunction("0");

      blk.addField( "DUMMY1" ).
      setHidden().
      setFunction("0");

      blk.addField( "DUMMY2" ).
      setHidden().
      setFunction("''");

      blk.addField( "DUMMY3" ).
      setHidden().
      setFunction("''");


      blk.addField("REV_NO").
      setSize(10).
      setHidden().
      setFunction("''");

      blk.addField( "HAS_FILE" ).
      setHidden().
      setFunction("''");

      blk.addField( "DOC_REF_EXIST" ).
      setHidden().
      setFunction("''");

      blk.addField("NEW_REVISION").
      setHidden().
      setFunction("''");


      blk.addField("NEW_DOC_SHEET").
      setSize(7).
      setMandatory().
      setHidden().
      setLabel("DOCMAWNEWSHEETWIZARDSNEWDOCSHEET: New Sheet Number").
      setFunction("''").
      setUpperCase();

      blk.addField("DOC_REV_TEXT").
      setSize(48).
      setHeight(3).
      setMandatory().
      setHidden().
      setLabel("REVISIONNOTE : Issue Revision Note").
      setFunction("''");

      blk.addField("OLD_PART_REV").
      setHidden().
      setFunction("''");

      blk.addField("NEW_PART_SHEET").
      setHidden().
      setFunction("''");

      blk.addField("OUTSTR_1").
      setHidden().
      setFunction("''");

      blk.addField("INSTR_1").
      setHidden().
      setFunction("''");

      blk.setView("DOC_ISSUE_REFERENCE");
      blk.defineCommand("DOC_ISSUE_API","New__,Modify__,Remove__");
      blk.setTitle("Create New revision");
      eval(blk.generateAssignments());
      bar = mgr.newASPCommandBar(blk);

      headset = blk.getASPRowSet();

      tbl = mgr.newASPTable(blk);

      lay = blk.getASPBlockLayout();
      lay.setDefaultLayoutMode(lay.CUSTOM_LAYOUT);
      lay.setEditable();
      lay.setDialogColumns(1);


      // *********************************** DLG 2 ***********************************

      item2blk = mgr.newASPBlock("ITEM2");

      item2blk.addField("ITEM2_OBJID").
      setHidden().
      setDbName("OBJID");

      item2blk.addField("ITEM2_OBJVERSION").
      setHidden().
      setDbName("OBJVERSION");

      item2blk.addField( "ITEM2_DOC_CLASS").
      setHidden().
      setDbName("DOC_CLASS");

      item2blk.addField( "ITEM2_DOC_NO" ).
      setHidden().
      setDbName("DOC_NO");
      //Bug Id 71615, Start
      item2blk.addField( "ITEM2_DOC_REV" ).
      setHidden().
      setDbName("DOC_REV");
      //Bug Id 71615, End


      item2blk.addField(" LU_NAME").
      setHidden();


      item2blk.addField("KEY_REF").
      setHidden();


      item2blk.addField("DOC_OBJECT").
      setSize(15).
      setLabel("DOCMAWNEWSHEETWIZARDOBJECT: Object").
      setReadOnly().
      setFunction("OBJECT_CONNECTION_SYS.Get_Logical_Unit_Description(:LU_NAME)");


      item2blk.addField("DOC_OBJECT_KEY").
      setSize(20).
      setLabel("DOCMAWNEWSHEETWIZARDOBJECTKEY: Object Key").
      setReadOnly().
      setFunction("OBJECT_CONNECTION_SYS.Get_Instance_Description(:LU_NAME,NULL,:KEY_REF)");


      item2blk.addField("DOC_OBJECT_DESC").
      setSize(100).
      setReadOnly().
      setLabel("DOCMAWNEWSHEETWIZARDOBJDESC: Object Description");



      item2blk.addField("SURVEY_LOCKED_FLAG").
      setSize(200).
      setReadOnly().
      setLabel("DOCMAWNEWSHEETWIZARDSURVEYLOCKEDFLAG: Doc Connection Status");


      item2blk.addField("SURVEY_LOCKED_FLAG_DB").
      setHidden();


      item2blk.addField("KEEP_LAST_DOC_REV").
      setSize(200).
      setReadOnly().
      setLabel("DOCMAWNEWSHEETWIZARDUPDREV: Update Revision");


      item2blk.addField("KEEP_LAST_DOC_REV_DB").
      setHidden();



      item2blk.setView("DOC_REFERENCE_OBJECT");
      item2blk.defineCommand("DOC_REFERENCE_OBJECT_API","New__,Modify__,Remove__");
      item2blk.setMasterBlock(blk);
      //item2blk.setTitle("Create New revision");

      item2bar = mgr.newASPCommandBar(item2blk);
      item2bar.defineCommand(item2bar.OKFIND,   "okFindITEM2");
      item2bar.disableCommand(item2bar.NEWROW);

      item2bar.addCustomCommand("doSelect", mgr.translate("DOCMAWNEWSHEETWIZARDSELECT: Select..."));

      item2set = item2blk.getASPRowSet();

      item2tbl = mgr.newASPTable(item2blk);
      item2tbl.disableQuickEdit();
      item2tbl.enableRowSelect();
      item2tbl.disableEditProperties();

      item2lay = item2blk.getASPBlockLayout();
      item2lay.setDefaultLayoutMode(item2lay.MULTIROW_LAYOUT);
      item2lay.unsetAutoLayoutSelect();

      //Bug Id 69329, start
      // *********************************** DLG 3 ***********************************
      // This shows as the second step when the number of object connections exeed 100

      item3blk = mgr.newASPBlock("ITEM3");

      item3blk.addField( "ITEM3_NO_OF_OBJ_CON" ).
      setHidden();

      item3bar = mgr.newASPCommandBar(item3blk);

      item3set = item2blk.getASPRowSet();

      item3tbl = mgr.newASPTable(item3blk);
      item3tbl.disableQuickEdit();
      item3tbl.enableRowSelect();
      item3tbl.disableEditProperties();

      item3lay = item3blk.getASPBlockLayout();
      item3lay.setDefaultLayoutMode(item3lay.MULTIROW_LAYOUT);
      item3lay.unsetAutoLayoutSelect();
      //Bug Id 69329, end

      super.preDefine();
   }


   public void  adjust()
   {
      ASPManager mgr = getASPManager();

      mgr.getASPField("NEW_REVISIONS").setHidden();
      mgr.getASPField("INFO").setHidden();

   }


   //===============================================================
   //  HTML
   //===============================================================
   protected String getDescription()
   {
      return "DOCMAWNEWSHEETWIZARDTITLE: Create New Sheet";
   }

   protected String getTitle()
   {
      return "DOCMAWNEWSHEETWIZARDTITLE: Create New Sheet";
   }

   protected AutoString getContents() throws FndException
   {
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>");
      out.append(mgr.generateHeadTag("DOCMAWNEWSHEETWIZARDTITLE: Create New Sheet"));
      out.append("<title></title>\n");
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">\n");
      out.append(mgr.startPresentation("DOCMAWNEWSHEETWIZARDTITLE: Create New Sheet"));
      out.append("<input type=\"hidden\" name=\"SELECTED_VALUE\" value=\"");
      out.append(mgr.HTMLEncode(selected_value));//Bug Id 72326
      out.append("\">\n");
      out.append("<input type=\"hidden\" name=\"SELECTED_VALUE1\" value=\"");
      out.append(nextSheetSelected);
      out.append("\">\n");
      if (activeFrame0)
      {
         out.append("  <input type=\"hidden\" name=\"FIN\" value>\n");
         out.append("<table border=\"0\" bgcolor=\"white\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"1\">\n");
         out.append("    <tr>\n");
         out.append("      <td  ><img src = \"../docmaw/images/CreateNewDocRevision.jpg\"></td>\n");
         out.append("      <td><table align=\"top\" border=\"0\" bgcolor=\"white\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"1\" width=\"400\">\n");
         out.append("        <tr>\n");
         out.append(fmt.drawReadLabel("DOCMAWNEWSHEETWIZARDWIZHEADER: New Sheet Wizard for Document Issue :<p>"));
         out.append(fmt.drawReadLabel(wizHeader));
         out.append("        </tr>\n");
         out.append("      </table>\n");
         out.append("      <table border=\"0\" bgcolor=\"white\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"1\" width=\"600\">\n");
         out.append("        <tr>\n");
         out.append("          <td><hr>\n");
         out.append("          </td>\n");
         out.append("        </tr>\n");
         out.append("        <tr>\n");
         out.append("          <td></td>\n");
         out.append("        </tr>\n");
         out.append("      </table>\n");
         out.append("      <table border=\"0\" bgcolor=\"white\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"1\" width=\"400\">\n");
         out.append("        <tr>\n");
         out.append("          <td>");
         out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDCREATETITLEREVISION: Change Revision on Title?"));
         out.append("</td>\n");
         out.append("          <td>");
         out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDYESNO: Yes"));
         out.append(fmt.drawCheckbox("CREATENEWTITLEREV","OFF",false,"onClick=setValue()"));
         out.append("          </td>\n");
         out.append("        </tr>\n");
         out.append("        <tr>\n");
         out.append("          <td>");
         out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDEXISTTITLEREV: Existing Title Revision"));
         out.append("</td>\n");
         out.append("          <td>");
         out.append(fmt.drawReadOnlyTextField("REVISION",revision,""));
         out.append("</td>\n");
         out.append("        </tr>\n");
         out.append("        <tr>\n");
         out.append("          <td>");
         out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDNEWTITLEREV: New Title Revision"));
         out.append("</td>\n");
         out.append("          <td>");
         out.append(fmt.drawTextField("NEWREV","","",0,6)); //Bug 67230, Added parameters size and length.
         out.append("</td>\n");
         out.append("        </tr>\n");
         out.append("        <tr>\n");
         out.append("          <td>");
         out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDNEWTITLEREVNOTE: Title Revisions Note"));
         out.append("</td>\n");
         out.append("          <td>");
         out.append(fmt.drawTextField("NEWREVNOTE","",""));
         out.append("</td>\n");
         out.append("        </tr>\n");
         out.append("        <tr>\n");
         out.append("          <td align=\"right\">");
         out.append(lay.generateDialog());
         out.append("</td>\n");
         out.append("          <td></td>\n");
         out.append("        </tr>\n");
         out.append("        <tr>\n");
         out.append("          <td><br>\n");
         out.append("          </td>\n");
         out.append("        </tr>\n");
         out.append("      </table>\n");
         out.append("      <table border=\"0\" bgcolor=\"white\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"1\" width=\"600\">\n");
         out.append("        <tr>\n");
         out.append("          <td><hr>\n");
         out.append("          </td>\n");
         out.append("        </tr>\n");
         out.append("        <tr>\n");
         out.append("          <td></td>\n");
         out.append("        </tr>\n");
         out.append("      </table>\n");
         out.append("      <table border=\"0\" bgcolor=\"white\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"1\" width=\"600\">\n");
         out.append("        <tr>\n");
         out.append("          <td>");
         out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDSELECTPREVIOUSSHEET: Select previous sheet number"));
         out.append("</td>\n");
         out.append("          <td>");
         out.append(fmt.drawSelect("DOC_SHEET_OLD1", ctx.readBuffer("REV_SELECT_BUFF"), selected_value,"OnChange=updateAllValues()"));
         out.append("</td>\n");
         out.append("        </tr>\n");
         out.append("        <tr>\n");
         out.append("          <td>");
         out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDNEWSHEET: New Sheet Number"));
         out.append("</td>\n");
         out.append("          <td>");
         out.append(fmt.drawTextField("NEWSHEET",mgr.HTMLEncode(new_sheet),"OnChange=toUpper_('NEWSHEET',-1)"));//Bug Id 72326 //Bug Id 77083 toUpper_
         out.append("</td>\n");
         out.append("        </tr>\n");
         out.append("        <tr>\n");
         out.append("          <td>");
         out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDSELECTNEXTSHEET: Select next sheet number (when creating an 'in between' sheet)"));
         out.append("</td>\n");
         out.append("          <td>");
         out.append(fmt.drawSelect("NEXT_SHEET_NO", ctx.readBuffer("REV_SELECT_BUFF1"), nextSheetSelected,""));
         out.append("</td>\n");
         out.append("        </tr>\n");
         out.append("        <tr>\n");
         out.append("          <td>");
         out.append(fmt.drawWriteLabel("DOCMAWSHEETWIZARDSHEETORDER: Sheet Order"));
         out.append("</td>\n");
         out.append("          <td>");
         out.append(fmt.drawTextField("NEWSORDER",s_order,""));
         out.append("</td>\n");
         out.append("        </tr>        \n");
         out.append("        <tr>\n");
         out.append("          <td>");
         out.append(fmt.drawWriteLabel("DOCMAWSHEETWIZARDREVTEXT: Revision Text"));
         out.append("</td>\n");
         out.append("          <td>");
         out.append(fmt.drawTextField("NEWRNOTE","",""));
         out.append("</td>\n");
         if (showDetail)
         {
            out.append("        </tr>\n");
            out.append("      </table>\n");
            out.append("      <table  border=\"0\" bgcolor=\"white\"  class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"1\" width= \"600\">\n");
            out.append("      <tr><td><hr></td></tr>\n");
            out.append("       <tr>\n");
            out.append("         <td>");
            out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDCOPYSHEET: Make the new sheet a copy of this sheet?"));
            out.append("</td>\n");
            out.append("         <td>");
            out.append(fmt.drawCheckbox("CBCOPYSHEET", "ON",copy_val,"OnClick=changeColor()"));
            out.append("</td>\n");
            out.append("      </tr>\n");
            out.append("      <tr>\n");
            out.append("         <td><U>");
            out.append(fmt.drawReadLabel(mgr.translate("DOCMAWNEWSHEETWIZARDOLDVALUES: From Old")));
            out.append("</U></td>\n");
            out.append("      </tr>\n");
            out.append("      <tr><td></td></tr>\n");
            out.append("      <!--/table-->\n");
            out.append("      <!--table  border=0 bgcolor=white  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= \"500\"-->\n");
            out.append("      <tr>\n");
            out.append("         <td>");
            out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDCPSTR: Copy Structure"));
            out.append("</td>\n");
            out.append("         <td>");
            out.append(fmt.drawCheckbox("CBCOPYSTRUCTURE","ON",structure_val,"OnClick=structureCheck()"));
            out.append("</td>\n");
            out.append("      </tr>\n");
            out.append("      <tr>\n");
            out.append("         <td>");
            out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDCPFIL: Copy File"));
            out.append("</td>\n");
            out.append("         <td>");
            out.append(fmt.drawCheckbox("CBCOPYFILE","ON",file_val,"OnClick=fileCheck()"));
            out.append("</td>\n");
            out.append("      </tr>\n");
            out.append("      <tr>\n");
            out.append("         <td>");
            out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDCPAPPROUTE: Copy Approval Process"));
            out.append("</td>\n");
            out.append("         <td>");
            out.append(fmt.drawCheckbox("CBCOPYDOCUMENTSURVEY","ON",copyapp_val,"OnClick=surveyCheck()"));
            out.append("</td>\n");
            out.append("      </tr>\n");
            out.append("      <tr>\n");
            out.append("         <td>");
            out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDCPACCESS: Copy Access"));
            out.append("</td>\n");
            out.append("         <td>");
            out.append(fmt.drawCheckbox("CBCOPYACCESS","ON",access_val,"OnClick=accCheck()"));
            out.append("</td>\n");
            out.append("      </tr>\n");
            out.append("      <tr>\n");
            out.append("         <td>");
            out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDCOPYREV: Copy Record Data"));
            out.append("</td>\n");
            out.append("         <td>");
            out.append(fmt.drawCheckbox("CBCOPYREC", "ON",rec_val,"OnClick=copyrecCheck()"));
            out.append("</td>\n");
            out.append("      </tr>\n");
            out.append("      <tr>\n");
            out.append("         <td>");
            out.append(fmt.drawWriteLabel("DOCMAWNEWSHEETWIZARDSETREV: Set Revision to first Revision on the Sheet"));
            out.append("</td>\n");
            out.append("         <td>");
            out.append(fmt.drawCheckbox("CBSETREV","ON",revision_val,"OnClick=revisionCheck()"));
            out.append("</td>\n");
         }
         out.append("      </tr>\n");
         out.append("      </table>\n");
         out.append("      </td>\n");
         out.append("    </tr>\n");
         out.append("  </table>\n");
         out.append("  <table>\n");
         out.append("  </table>\n");
         out.append("  <table>\n");
         out.append("    <tr>\n");
         out.append("      <td align=\"right\" width=\"628\">");
         out.append(fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWNEWSHEETWIZARDCANCELL: Cancel"),""));
         out.append("&nbsp;&nbsp; \n");
         if (showDetail)
         {
            out.append(fmt.drawButton("NXT",mgr.translate("DOCMAWNEWSHEETWIZARDNEXT:  Next> "),"OnClick=checkEmptyFields('nxt')"));
            out.append("&nbsp;&nbsp;\n");
         }
         out.append(fmt.drawButton("FINISH0",mgr.translate("DOCMAWNEWSHEETWIZARDFINISHH: Finish"),"OnClick=checkEmptyFields('fin')"));
         out.append("</td>\n");
         out.append("      <input type=\"hidden\" name=\"NEXT\" value=\"\">\n");
         out.append("    </tr>\n");
         out.append("  </table>\n");
         appendDirtyJavaScript("        document.form.NEWREV.readOnly=true;\n");
         appendDirtyJavaScript("        document.form.NEWREVNOTE.readOnly=true;\n");
         appendDirtyJavaScript("        setValue();\n");
         appendDirtyJavaScript("        function setValue()\n");
         appendDirtyJavaScript("        {\n");
         appendDirtyJavaScript("                if (document.form.CREATENEWTITLEREV.checked==true)\n");
         appendDirtyJavaScript("                {\n");
         appendDirtyJavaScript("                        document.form.NEWREV.readOnly=false;\n");
         appendDirtyJavaScript("                        document.form.NEWREVNOTE.readOnly=false;\n");
         appendDirtyJavaScript("                        document.form.NEWREVNOTE.style.backgroundColor=\"#ffffff\";\n");
         appendDirtyJavaScript("                        document.form.NEWREV.style.backgroundColor=\"#ffffff\";\n");
         appendDirtyJavaScript("                }\n");
         appendDirtyJavaScript("                else\n");
         appendDirtyJavaScript("                {\n");
         appendDirtyJavaScript("                        document.form.NEWREVNOTE.readOnly=true;\n");
         appendDirtyJavaScript("                        document.form.NEWREV.readOnly=true;\n");
         appendDirtyJavaScript("                        document.form.NEWREVNOTE.style.backgroundColor=\"#cccccc\";\n");
         appendDirtyJavaScript("                        document.form.NEWREV.style.backgroundColor=\"#cccccc\";\n");
         appendDirtyJavaScript("                }\n");
         appendDirtyJavaScript("        }\n");
         if (showDetail)
         {
            appendDirtyJavaScript("        changeColor();\n");
            appendDirtyJavaScript("        function changeColor()\n");
            appendDirtyJavaScript("        {\n");
            appendDirtyJavaScript("                if (document.form.CBCOPYSHEET.checked==true)\n");
            appendDirtyJavaScript("                {\n");
            appendDirtyJavaScript("                        document.form.CBCOPYSTRUCTURE.style.visibility = \"visible\";\n");
            appendDirtyJavaScript("                        document.form.CBCOPYACCESS.style.visibility = \"visible\";\n");
            appendDirtyJavaScript("                        document.form.CBCOPYFILE.style.visibility = \"visible\";\n");
            appendDirtyJavaScript("                        document.form.CBCOPYDOCUMENTSURVEY.style.visibility = \"visible\";\n");
            appendDirtyJavaScript("                        document.form.CBSETREV.style.visibility = \"visible\";\n");
            appendDirtyJavaScript("                        document.form.CBCOPYREC.style.visibility = \"visible\";\n");
            appendDirtyJavaScript("                }\n");
            appendDirtyJavaScript("                else\n");
            appendDirtyJavaScript("                {\n");
            appendDirtyJavaScript("                        document.form.CBCOPYSTRUCTURE.style.visibility = \"hidden\";\n");
            appendDirtyJavaScript("                        document.form.CBCOPYACCESS.style.visibility = \"hidden\";\n");
            appendDirtyJavaScript("                        document.form.CBCOPYFILE.style.visibility = \"hidden\";\n");
            appendDirtyJavaScript("                        document.form.CBCOPYDOCUMENTSURVEY.style.visibility = \"hidden\";\n");
            appendDirtyJavaScript("                        document.form.CBSETREV.style.visibility = \"hidden\";\n");
            appendDirtyJavaScript("                        document.form.CBCOPYREC.style.visibility = \"hidden\";\n");
            appendDirtyJavaScript("                }       \n");
            appendDirtyJavaScript("        }\n");
         }
      }
      if (activeFrame1)
      {
         out.append("   <table  border=0 bgcolor=white  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= \"500\">\n");
         out.append("        <tr>\n");
         out.append("                <td>");
         out.append(lay.generateDialog());
         out.append("<td>\n");
         out.append("        </tr>\n");
         out.append("        <tr>\n");
         out.append(fmt.drawReadLabel("DOCMAWNEWSHEETWIZARDSELOBJS: Select object(s) to be connected to the new sheet:"));
         out.append("<p>\n");
         out.append("        </tr>\n");
         out.append("        <tr>\n");
         out.append(item2lay.generateDataPresentation());
         out.append("        </tr>\n");
         out.append("        <tr>\n");
         out.append("        </tr>\n");
         out.append("   </table>\n");
         out.append("   <table border=0>\n");
         out.append("      <tr><td><br></td></tr>\n");
         out.append("      <tr>\n");
         out.append("        <td align=\"left\" width=\"400\">");
         out.append(fmt.drawButton("SELALL",mgr.translate("DOCMAWNEWSHEETWIZARDSELALL: Select All"),"OnClick='selectUnselectAll(true);'"));
         out.append("&nbsp;&nbsp;\n");
         out.append(fmt.drawButton("UNSELALL",mgr.translate("DOCMAWNEWSHEETWIZARDUNSELALL: Clear Selection"),"OnClick='selectUnselectAll(false);'"));
         out.append("&nbsp;&nbsp;</td>\n");
         out.append("      </tr>\n");
         out.append("      <tr>\n");
         out.append("      <td align=\"right\" width=680>\n");
         out.append(fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWNEWSHEETWIZARDCANCELL: Cancel"),""));
         out.append("&nbsp;&nbsp;\n");
         out.append(fmt.drawSubmit("PREVIOUS",mgr.translate("DOCMAWNEWSHEETWIZARDPREVIOUS: <Previous"),""));
         out.append("&nbsp;&nbsp;\n");
         out.append(fmt.drawSubmit("FINISH",mgr.translate("DOCMAWNEWSHEETWIZARDFINISHH: Finish"),""));
         out.append("&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
         out.append("      </tr>\n");
         out.append("    </table>\n");
      }

      //Bug Id 69329, start
      // This shows as the second step when the number of object connections exeed 100

      if (activeFrame2)
      {
	 String sRadioButton = ctx.readValue("RADIOBUTTON");

         out.append("   <table  border=0 bgcolor=white  class=\"BlockLayoutTable\" cellspacing=0 cellpadding=1 width= \"500\">\n");
         out.append("   <tr align=\"center\"><td>\n");
	 out.append(	fmt.drawReadLabel("DOCMAWNEWSHEETWIZARDOBJCONTITLE: Object Connections"));
         out.append("   </td></tr>");
         out.append("   <tr ><td>\n");
         out.append("   &nbsp;&nbsp;<!-- leave a line here -->\n");
         out.append("   </td></tr>\n");

	 out.append("   <tr align=\"left\"><td>\n");
	 out.append(    fmt.drawReadLabel("DOCMAWNEWSHEETWIZARDOBJCONNOTE1: There are more connected objects than can be displayed in the wizard."));
	 out.append("   </td></tr>\n");

	 out.append("   <tr align=\"left\"><td>\n");
	 out.append(    fmt.drawReadLabel("DOCMAWNEWSHEETWIZARDOBJCONNOTE1: Use the radio buttons below to select how to treat the connected objects:"));
	 out.append("   </td></tr>\n");

	 out.append("   <tr align=\"left\"><td>\n");
	 out.append("   &nbsp;&nbsp;&nbsp;&nbsp;" + fmt.drawRadio(mgr.translate("DOCMAWNEWSHEETWIZARDCOPYALL: Copy All Connections"),"COPYALL", "COPYALL", "COPYALL".equals(sRadioButton), "OnClick=\"javascript:document.form.COPYALL.checked=true;document.form.NOCOPY.checked=false;\""));
	 out.append("   </td></tr>\n");

	 out.append("   <tr align=\"left\"><td>\n");
	 out.append("   &nbsp;&nbsp;&nbsp;&nbsp;" + fmt.drawRadio(mgr.translate("DOCMAWNEWSHEETWIZARDNOCOPY: Don't Copy Connections"),"NOCOPY", "NOCOPY", "NOCOPY".equals(sRadioButton), "OnClick=\"javascript:document.form.COPYALL.checked=false;document.form.NOCOPY.checked=true;\""));
	 out.append("   </td></tr>\n");

         out.append("   <tr><td>\n");
         out.append(    item3lay.generateDataPresentation());
         out.append("   </tr></td>\n");

         out.append("   </table>\n");
         out.append("   <table border=0>\n");
         out.append("      <tr><td><br></td></tr>\n");
         out.append("      <tr>\n");
         out.append("      <td align=\"right\" width=500>\n");
         out.append(fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWNEWSHEETWIZARDCANCELL: Cancel"),""));
         out.append("&nbsp;&nbsp;\n");
         out.append(fmt.drawSubmit("PREVIOUS",mgr.translate("DOCMAWNEWSHEETWIZARDPREVIOUS: <Previous"),""));
         out.append("&nbsp;&nbsp;\n");
         out.append(fmt.drawSubmit("FINISH",mgr.translate("DOCMAWNEWSHEETWIZARDFINISHH: Finish"),""));
         out.append("&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
         out.append("      </tr>\n");
         out.append("    </table>\n");
      }
      //Bug Id 69329, end

      appendDirtyJavaScript("//=============================================================================\n");
      appendDirtyJavaScript("//   CLIENT FUNCTIONS\n");
      appendDirtyJavaScript("//=============================================================================\n");
      appendDirtyJavaScript("//document.form.REVISION.style.backgroundColor=\"#cccccc\";\n");
      appendDirtyJavaScript("meth_val='1';\n");
      appendDirtyJavaScript("function structureCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if('");
      appendDirtyJavaScript(structure_checkable);
      appendDirtyJavaScript("' == 'false')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYSTRUCTURE.checked=false;\n");
      appendDirtyJavaScript("      //document.form.CBCOPYSTRUCTURE.style.backgroundColor=\"#cccccc\";\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDOPTIONNOTAVAILABLE: This option is not available!"));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("  else  if (document.form.CBCOPYSHEET.checked==false)\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("      document.form.CBCOPYSTRUCTURE.checked=true;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDCHOOSECOPYOPTION: Choose the option to copy from this sheet!"));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function accCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if('");
      appendDirtyJavaScript(access_checkable);
      appendDirtyJavaScript("' == 'false')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYACCESS.checked=false;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDOPTIONNOTAVAILABLE: This option is not available!"));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else  if (document.form.CBCOPYSHEET.checked==false)\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("      document.form.CBCOPYACCESS.checked=true;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDCHOOSECOPYOPTION: Choose the option to copy from this sheet!"));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function fileCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if('");
      appendDirtyJavaScript(file_checkable);
      appendDirtyJavaScript("' == 'false')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYFILE.checked=false;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDOPTIONNOTAVAILABLE: This option is not available!"));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else  if (document.form.CBCOPYSHEET.checked==false)\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("      document.form.CBCOPYFILE.checked=true;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDCHOOSECOPYOPTION: Choose the option to copy from this sheet!"));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }    \n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function surveyCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if ('");
      appendDirtyJavaScript(docsurvey_checkable);
      appendDirtyJavaScript("'=='false')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYDOCUMENTSURVEY.checked=false;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDOPTIONNOTAVAILABLE: This option is not available!"));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else  if (document.form.CBCOPYSHEET.checked==false)\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("      document.form.CBCOPYDOCUMENTSURVEY.checked=true;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDCHOOSECOPYOPTION: Choose the option to copy from this sheet!"));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function setAgain()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("        document.form.NEWREV.readOnly=true;\n");
      appendDirtyJavaScript("        document.form.NEWREVNOTE.readOnly=true;\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function revisionCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  if (document.form.CBCOPYSHEET.checked==false)\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("      document.form.CBSETREV.checked=true;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDCHOOSECOPYOPTION: Choose the option to copy from this sheet!"));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("  }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function preCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  if (document.form.CBCOPYSHEET.checked==false)\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("      document.form.CBSETSTATE.checked=true;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDCHOOSECOPYOPTION: Choose the option to copy from this sheet!"));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function copyrecCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("  if (document.form.CBCOPYSHEET.checked==false)\n");
      appendDirtyJavaScript("  {\n");
      appendDirtyJavaScript("      //document.form.CBCOPYREC.checked=true;   \n");
      appendDirtyJavaScript("      document.form.CBCOPYREC.checked=true;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDCHOOSECOPYOPTION: Choose the option to copy from this sheet!"));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function updateAllValues()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (document.form.DOC_SHEET_OLD1.value==\"\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.DOC_SHEET_OLD1.value = document.form.SELECTED_VALUE.value;\n");
      appendDirtyJavaScript("      msg ='");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDYOUMUSTSELECTASHEET: You must select a sheet!"));
      appendDirtyJavaScript("'\n");
      appendDirtyJavaScript("      alert(msg);\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.SELECTED_VALUE.value = document.form.DOC_SHEET_OLD1.value;\n");
      appendDirtyJavaScript("     /* document.form.NEW_PART_REV.value = document.form.NP_REV.value;\n");
      appendDirtyJavaScript("      document.form.JOURNAL_NOTE.value = document.form.JNOTE.value; */\n");
      appendDirtyJavaScript("      submit();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function checkEmptyFields(str)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if (document.form.NEWSHEET.value==\"\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("        alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDSFSHEET: New Sheet No field requires a value"));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("       return;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   if (document.form.CREATENEWTITLEREV.checked==true)\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("      if (document.form.NEWREV.value==\"\")\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("          alert('");
      appendDirtyJavaScript(mgr.translateJavaScript("DOCMAWNEWSHEETWIZARDSNEWTITLEREV: New Title Revisoin field requires a value"));
      appendDirtyJavaScript("');\n");
      appendDirtyJavaScript("       return;\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("       if (str==\"nxt\")\n");
      appendDirtyJavaScript("          document.form.NEXT.value=\"NEXT\";\n");
      appendDirtyJavaScript("       else if (str==\"fin\")\n");
      appendDirtyJavaScript("          document.form.FIN.value=\"FIN\";\n");
      appendDirtyJavaScript("       submit();\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function selectUnselectAll(condition)\n");
      appendDirtyJavaScript("{ // selects or unselects rows depending on condition\n");
      appendDirtyJavaScript("        quit=false\n");
      appendDirtyJavaScript("        x=0;\n");
      appendDirtyJavaScript("        len=document.form.elements.length;\n");
      appendDirtyJavaScript("        do\n");
      appendDirtyJavaScript("        {\n");
      appendDirtyJavaScript("                if ((document.form.elements[x].name==\"__SELECTED2\") || (x==len))\n");
      appendDirtyJavaScript("                {\n");
      appendDirtyJavaScript("                        quit=true\n");
      appendDirtyJavaScript("                        break;\n");
      appendDirtyJavaScript("                }\n");
      appendDirtyJavaScript("                x++;\n");
      appendDirtyJavaScript("        }\n");
      appendDirtyJavaScript("        while(quit==false)\n");
      appendDirtyJavaScript("        quit=false\n");
      appendDirtyJavaScript("        y=x;\n");
      appendDirtyJavaScript("        do\n");
      appendDirtyJavaScript("        {\n");
      appendDirtyJavaScript("                if (document.form.elements[y].name!=\"__SELECTED2\")\n");
      appendDirtyJavaScript("                {\n");
      appendDirtyJavaScript("                        quit=true\n");
      appendDirtyJavaScript("                        break;\n");
      appendDirtyJavaScript("                }\n");
      appendDirtyJavaScript("                y++;\n");
      appendDirtyJavaScript("        }\n");
      appendDirtyJavaScript("        while(quit==false)\n");
      appendDirtyJavaScript("        for (z=x;z<y;z++)\n");
      appendDirtyJavaScript("        {\n");
      appendDirtyJavaScript("                document.form.elements[z].checked=condition;\n");
      appendDirtyJavaScript("        }\n");
      appendDirtyJavaScript("}\n");
      //Bug Id 74957, start
      if (!bPrevious && activeFrame1) 
      {
         if (item2set.countRows() > 0) // Bug Id 92069
	         appendDirtyJavaScript("   selectUnselectAll(true);\n");
      }
      //Bug Id 74957, end
      out.append(mgr.endPresentation());
      out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      return out;
   }
}
