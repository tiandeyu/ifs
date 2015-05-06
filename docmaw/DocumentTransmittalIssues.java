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
*  File        : DocumentTransmittalIssues.java
*  MODIFIED    :
*    BAKALK   2007-01-02   Created.
*    BAKALK   2007-03-27   Removed asp field : DOC_SURVERY_STATUS
*    CHSELK   2007-03-28   Added asp field : DOC_TRANSMITTAL_LINE_STATUS
*    BAKALK   2007-05-10   Call Id 143075, Removed asp field : COMMENT_FILE_ID and COMMENT_RECEIVED_ID. Added new asp field COMMENT_RECEIVED
*    BAKALK   2007-05-15   Call Id: 140755, Added 2 new commands and 2 links.
*    BAKALK   2007-06-07   Call Id: 141463, Removed 'our' prefix from some labels.
*    JANSLK   2007-08-08   Changed all non-translatable constants.
*    AMNALK   2007-12-12   Bug Id 68742, Changed the name of the page title
*    AMNALK   2008-09-18   Bug Id 76248, Modified adjust() and preDefine().
*    SHTHLK   2009-07-03   Bug Id: 84461, Set the length of TRANSMITTAL_ID to 120
* ----------------------------------------------------------------------------
*/
package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocumentTransmittalIssues extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocumentTransmittalIssues");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext       ctx;
   private ASPBlock       headblk;
   private ASPRowSet      headset;
   private ASPCommandBar  headbar;
   private ASPTable       headtbl;
   private ASPBlockLayout headlay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================

   private ASPTransactionBuffer trans;
   private ASPCommand           cmd;
   private ASPQuery             q;
   private ASPBuffer            data;
   private ASPBuffer            keys;
   private ASPBuffer            transferBuffer;



   //===============================================================
   // Construction
   //===============================================================
   public DocumentTransmittalIssues(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   public void  preDefine()
   {
      
      ASPManager mgr = getASPManager();


      headblk = mgr.newASPBlock("HEAD");

      headblk.disableDocMan();

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      //Bug Id 84461, Set the length to 120
      headblk.addField("TRANSMITTAL_ID").
      setSize(20).
      setMaxLength(120).
      setReadOnly().
      setInsertable().
      setUpperCase().
      setDynamicLOV("DOCUMENT_TRANSMITTAL").
      setCustomValidation("TRANSMITTAL_ID","TRANSMITTAL_DESCRIPTION").
      setSecureHyperlink("DocTransmittalInfo.page", "TRANSMITTAL_ID").
      setLabel("DOCUMENTTRANSMITTALISSUESTRANSMITTALID: Transmittal Id");

      


      headblk.addField("TRANSMITTAL_DESCRIPTION").
      setFunction("DOCUMENT_TRANSMITTAL_API.Get_Transmittal_Description(:TRANSMITTAL_ID)").
      setSize(20).
      setReadOnly().
      setMaxLength(800).
      setLabel("DOCUMENTTRANSMITTALISSUESTRANSDESC: Transmittal Description");



      headblk.addField("TRANSMITTAL_LINE_NO","Number").
      setSize(20).
      setReadOnly().
      setUpperCase().
      setLabel("DOCUMENTTRANSMITTALISSUESTRANSMITTALLINENO: Transmittal Line No");
      

      

      headblk.addField("DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setCustomValidation("DOC_CLASS","SDOCNAME").
      setLabel("DOCUMENTTRANSMITTALISSUESDOCCLASS: Doc Class");

      headblk.addField("SDOCNAME").
      setSize(20).
      setReadOnly().
      setFunction("DOC_CLASS_API.GET_NAME(:DOC_CLASS)").
      setLabel("DOCUMENTTRANSMITTALISSUESSDOCNAME: Doc Class Desc");


      headblk.addField("DOC_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setDynamicLOV("DOC_TITLE","DOC_CLASS").
		setLabel("DOCUMENTTRANSMITTALISSUESDOCNO: Document Number");

		headblk.addField("DOC_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE_LOV1","DOC_CLASS,DOC_NO").
		setLabel("DOCUMENTTRANSMITTALISSUESDOCSHEET: Document Sheet");

		headblk.addField("DOC_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE","DOC_CLASS,DOC_NO,DOC_SHEET").
		setLabel("DOCUMENTTRANSMITTALISSUESDOCREV: Document Revision");

      mgr.getASPField("DOC_NO").setSecureHyperlink("DocIssue.page", "DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");


      headblk.addField("CUSTOMER_DOCUMENT_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCUMENTTRANSMITTALISSUESCUSTOMERDOCCLASS: Customer Doc Class");


      headblk.addField("CUSTOMER_DOCUMENT_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setDynamicLOV("DOC_TITLE","CUSTOMER_DOCUMENT_CLASS DOC_CLASS").
		setLabel("DOCUMENTTRANSMITTALISSUESCUSTOMERDOCNO: Customer Document Number");

		headblk.addField("CUSTOMER_DOCUMENT_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE_LOV1","CUSTOMER_DOCUMENT_CLASS DOC_CLASS,CUSTOMER_DOCUMENT_NO DOC_NO").
		setLabel("DOCUMENTTRANSMITTALISSUESCUSTOMERDOCSHEET: Customer Document Sheet");

		headblk.addField("CUSTOMER_DOCUMENT_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE","CUSTOMER_DOCUMENT_CLASS DOC_CLASS,CUSTOMER_DOCUMENT_NO DOC_NO,CUSTOMER_DOCUMENT_SHEET DOC_SHEET").
		setLabel("DOCUMENTTRANSMITTALISSUESCUSTOMERDOCREV: Customer Document Revision");


      headblk.addField("SUPPLIER_DOCUMENT_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCUMENTTRANSMITTALISSUESSUPPLIERDOCCLASS: Supplier Doc Class");


      headblk.addField("SUPPLIER_DOCUMENT_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setDynamicLOV("DOC_TITLE","SUPPLIER_DOCUMENT_CLASS DOC_CLASS").
		setLabel("DOCUMENTTRANSMITTALISSUESSUPPLIERDOCNO: Supplier Document Number");

		headblk.addField("SUPPLIER_DOCUMENT_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE_LOV1","SUPPLIER_DOCUMENT_CLASS DOC_CLASS,SUPPLIER_DOCUMENT_NO DOC_NO").
		setLabel("DOCUMENTTRANSMITTALISSUESSUPPLIERDOCSHEET: Supplier Document Sheet");

		headblk.addField("SUPPLIER_DOCUMENT_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE","SUPPLIER_DOCUMENT_CLASS DOC_CLASS,SUPPLIER_DOCUMENT_NO DOC_NO,SUPPLIER_DOCUMENT_SHEET DOC_SHEET").
		setLabel("DOCUMENTTRANSMITTALISSUESSUPPLIERDOCREV: Supplier Document Revision");



      headblk.addField("SUB_CONTRACTOR_DOCUMENT_CLASS").
      setSize(20).
      setMaxLength(12).
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setLabel("DOCUMENTTRANSMITTALISSUESSUBCONTRACTORDOCCLASS: Sub Contractor Doc Class");


      headblk.addField("SUB_CONTRACTOR_DOCUMENT_NO").
		setSize(20).
		setMaxLength(120).
		setUpperCase().
		setDynamicLOV("DOC_TITLE","SUB_CONTRACTOR_DOCUMENT_CLASS DOC_CLASS").
		setLabel("DOCUMENTTRANSMITTALISSUESSUBCONTRACTORDOCNO: Sub Contractor Document Number");

		headblk.addField("SUB_CONTRACTOR_DOCUMENT_SHEET").
		setSize(20).
		setMaxLength(10).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE_LOV1","SUB_CONTRACTOR_DOCUMENT_CLASS DOC_CLASS,SUB_CONTRACTOR_DOCUMENT_NO DOC_NO").
		setLabel("DOCUMENTTRANSMITTALISSUESSUBCONTRACTORDOCSHEET: Sub Contractor Document Sheet");

		headblk.addField("SUB_CONTRACTOR_DOCUMENT_REV").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setDynamicLOV("DOC_ISSUE","SUB_CONTRACTOR_DOCUMENT_CLASS DOC_CLASS,SUB_CONTRACTOR_DOCUMENT_NO DOC_NO,SUB_CONTRACTOR_DOCUMENT_SHEET DOC_SHEET").
		setLabel("DOCUMENTTRANSMITTALISSUESSUBCONTRACTORDOCREV: Sub Contractor Document Revision");

       
      headblk.addField("NOTE").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setLabel("DOCUMENTTRANSMITTALISSUESNOTE: Note");


      headblk.addField("COMMENT_RECEIVED_DATE","Date").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
		setLabel("DOCUMENTTRANSMITTALISSUESCOMMENTRECEIVEDDATE: Comment Received Date");

      /*headblk.addField("COMMENT_FILE_ID").
		setSize(20).
		setMaxLength(6).
		setReadOnly().
		setUpperCase().
		setLabel("DOCUMENTTRANSMITTALISSUESCOMMENTFILEID: Comment File Id");

      headblk.addField("COMMENT_RECEIVED_ID").
		setSize(20).
		setMaxLength(6).
		setReadOnly().
		setUpperCase().
		setLabel("DOCUMENTTRANSMITTALISSUESCOMMENTRECEIVEDID: Comment Received Id"); */

      headblk.addField("COMMENT_RECEIVED").
      setSize(20).
      setReadOnly().
      setCheckBox ("0,1").
      setLabel("DOCUMENTTRANSMITTALISSUESCOMMENTRECEIVED: Comment Received");

      ////
      /*headblk.addField("DOC_SURVERY_STATUS").
		setSize(20).  
		setMaxLength(6).
		setReadOnly().
		setLabel("DOCUMENTTRANSMITTALISSUESDOCSURVERYSTATUS: Document Transmittal Survey Status");*/

      headblk.addField("PROJECT_ID").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
                setReadOnly().
                //setHidden().
                setFunction("TRANSMITTAL_OBJ_CON_API.Get_Project_Id(:TRANSMITTAL_ID)").
		setLabel("DOCUMENTTRANSMITTALISSUESPROJECTID: Project Id");


      headblk.addField("DOC_TRANSMITTAL_LINE_STATUS").
		setSize(20).
		setMaxLength(6).
		setUpperCase().
                setDynamicLOV("TRANSMITTAL_LINE_STATE","PROJECT_ID",600,445).
		setLabel("DOCUMENTTRANSMITTALISSUESTRANSMITTALLINESTATE: Doc Transmittal Line Status");
      
      //Bug Id 76248, start
      headblk.addField("DB_STATE").
		setFunction("DOCUMENT_TRANSMITTAL_API.Finite_State_Encode__(DOCUMENT_TRANSMITTAL_API.Get_State(:TRANSMITTAL_ID))").
		setHidden();
      //Bug Id 76248, end

      headblk.setView("DOC_TRANSMITTAL_ISSUE"); 
      headblk.defineCommand("DOC_TRANSMITTAL_ISSUE_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);

      headbar.addCustomCommand("gotoDocInfo",mgr.translate("DOCUMENTTRANSMITTALISSUESDOCINFO: Document Info..."));
      headbar.addCustomCommand("gotoTransmittalInfo",mgr.translate("DOCUMENTTRANSMITTALISSUESTRANSINFO: Transmittal Info..."));

      headbar.enableMultirowAction();
      
      
      headtbl = mgr.newASPTable(headblk);      
      headtbl.setTitle(mgr.translate("DOCMAWDOCUMENTTRANSMITTALISSUESOVWDOCCLAFORTRANSMITTALS: Overview - Document Classes for Transmittals"));
      headtbl.enableRowSelect();
      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

   }//end of predefine


    public void  validate()
   {
      
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");

       if(val.equals("DOC_CLASS"))
      {
         trans.clear();
         cmd = trans.addCustomFunction("CLASSNAME", "DOC_CLASS_API.GET_NAME", "SDOCNAME");
         cmd.addParameter("DOC_CLASS");
         trans = mgr.validate(trans);

         String className         = trans.getValue("CLASSNAME/DATA/SDOCNAME");
         StringBuffer response = new StringBuffer("");
         response.append(className);
         response.append("^");
         mgr.responseWrite(response.toString());
      }
      else if (val.equals("TRANSMITTAL_ID")) {
         trans.clear();
         cmd = trans.addCustomFunction("TRNSDESC", "DOCUMENT_TRANSMITTAL_API.Get_Transmittal_Description", "TRANSMITTAL_DESCRIPTION");
         cmd.addParameter("TRANSMITTAL_ID");
         cmd = trans.addCustomFunction("PROJID", "TRANSMITTAL_OBJ_CON_API.Get_Project_Id", "PROJECT_ID");
         cmd.addParameter("TRANSMITTAL_ID");
         trans = mgr.validate(trans);

         String transDesc         = trans.getValue("TRNSDESC/DATA/TRANSMITTAL_DESCRIPTION");
         String projectId         = trans.getValue("PROJID/DATA/PROJECT_ID");
         StringBuffer response = new StringBuffer("");
         response.append(transDesc);
         response.append("^");
         response.append(projectId);
         debug("Hi ----- :" + projectId);
         response.append("^");
         mgr.responseWrite(response.toString());
      }
      mgr.endResponse();
    }


     public void  gotoDocInfo()
    {
         ASPManager mgr = getASPManager();

         headset.storeSelections();
         if (headlay.isSingleLayout())
            headset.selectRow();
         keys=headset.getSelectedRows("DOC_CLASS,DOC_NO,DOC_SHEET,DOC_REV");

         if (keys.countItems()>0)
         {
            mgr.transferDataTo("DocIssue.page",keys);
         }
         else
         {
            mgr.showAlert(mgr.translate("DOCUMENTTRANSMITTALISSUESNORECSEL: No records selected!"));
         }

   }


   public void  gotoTransmittalInfo()
   {
      ASPManager mgr = getASPManager();

      headset.storeSelections();
      if (headlay.isSingleLayout())
         headset.selectRow();
      keys=headset.getSelectedRows("TRANSMITTAL_ID");

      if (keys.countItems()>0)
      {
         mgr.transferDataTo("DocTransmittalInfo.page",keys);
      }
      else
      {
         mgr.showAlert(mgr.translate("DOCUMENTTRANSMITTALISSUESNORECSEL: No records selected!"));
      }

   }



    public void run()
   {
      ASPManager mgr = getASPManager();
      ctx     = mgr.getASPContext();

      trans = mgr.newASPTransactionBuffer();

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();

        adjust();
    }


     public void  adjust()
   {
      ASPManager mgr = getASPManager();

      if(headlay.isEditLayout() && headset.getValue("PROJECT_ID") == null)
      {
             mgr.getASPField("DOC_TRANSMITTAL_LINE_STATUS").setReadOnly();
      }

      // do any adjustments
      //Bug Id 76248, start
      if(headlay.isEditLayout() && !"Preliminary".equals(headset.getRow().getValue("DB_STATE")))
      {
          mgr.getASPField("DOC_CLASS").setReadOnly();
          mgr.getASPField("DOC_NO").setReadOnly();
          mgr.getASPField("DOC_SHEET").setReadOnly();
          mgr.getASPField("DOC_REV").setReadOnly();
      }
      //Bug Id 76248, end

     }
    //===============================================================
    //  HTML
    //===============================================================
    protected String getDescription()
    {
       //Bug Id 68742, start
       if (headlay.isMultirowLayout())
          return "DOCMAWDOCUMENTTRANSMITTALISSUESOVWDOCTRANSMITISSUES: Overview - Document Transmittal Document Revisions"; 
       else
          return "DOCMAWDOCUMENTTRANSMITTALISSUESDOCTRANSMITISSUES: Document Transmittal Document Revisions"; 
       //Bug Id 68742, end
       
    }

    protected String getTitle()
    {
       return getDescription(); //Bug Id 68742
    }



   protected void printContents() throws FndException
   {
      appendToHTML(headlay.show());
   }//end of printContents


   //=============================================================================
   //   CMDBAR FUNCTIONS
   //=============================================================================

   public void  okFind()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      q = trans.addQuery(headblk);
      if (mgr.dataTransfered())
      {
         //ASPBuffer buff = mgr.getTransferedData();
         q.addOrCondition(transferBuffer);
      }


      q.includeMeta("ALL");
      mgr.querySubmit(trans,headblk);

      if (headset.countRows() == 0)
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCTITLEOVWNODATA: No data found."));
         headset.clear();
      }


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


   public void newRow()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("HEAD","DOC_TRANSMITTAL_ISSUE_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }//end of newRow


}// end of class

