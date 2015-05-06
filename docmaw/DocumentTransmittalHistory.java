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
*  File        : DocumentTransmittalHistory.java
*  MODIFIED    :
*    BAKALK   2007-01-02   Created.
*    BAKALK   2007-05-15   Call Id: 140755, Added a new command and a link.
*    JANSLK   2007-08-07   Changed all non-translatable constants.
*    AMNALK   2007-12-12   Bug Id 68742, Changed the title of the page
*    AMNALK   2008-07-08   Bug Id 75082, Modified adjust() & okFind() to set the find layout if no records found.
*    AMCHLK   2008-08-12   Bug Id 78792, Removed Modify__() & Remove__() commands from headblk and modified some datafiled attributes of the headblk 
*    SHTHLK   2009-07-03   Bug Id: 84461, Set the length of TRANSMITTAL_ID to 120
* ----------------------------------------------------------------------------
*/
package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class DocumentTransmittalHistory extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.DocumentTransmittalHistory");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPContext       ctx;
   private ASPHTMLFormatter fmt;
   private ASPBlock         headblk;
   private ASPRowSet        headset;
   private ASPCommandBar    headbar;
   private ASPTable         headtbl;
   private ASPBlockLayout  headlay;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================

   private ASPTransactionBuffer trans;
   private ASPCommand           cmd;
   private ASPQuery             q;
   private ASPBuffer            data;
   private ASPBuffer            keys;
   private ASPBuffer            transferBuffer;

   private String sHistoryMode;



   //===============================================================
   // Construction
   //===============================================================
   public DocumentTransmittalHistory(ASPManager mgr, String page_path)
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
      setLabel("DOCUMENTTRANSMITTALHISTORYTRANSMITTALID: Transmittal Id");
      

      headblk.addField("TRANSMITTAL_DESCRIPTION").
      setFunction("DOCUMENT_TRANSMITTAL_API.Get_Transmittal_Description(:TRANSMITTAL_ID)").
      setSize(20).
      setMaxLength(800).
      setReadOnly().
      setLabel("DOCUMENTTRANSMITTALHISTORYTRANSDESC: Transmittal Description");

      headblk.addField("HISTORY_LOG_LINE_NO","Number").
      setSize(20).
      setReadOnly().    // Bug Id 78792
      setLabel("DOCUMENTTRANSMITTALHISTORYHISTORYLOGLINENO: History Log Line No");

      headblk.addField("DOC_TRANS_HISTORY_CAT").
      setSize(20).
      setMaxLength(400).
      setReadOnly().
      setSelectBox().
      enumerateValues("DOC_TRANS_HISTORY_CAT_API").
      setLabel("DOCUMENTTRANSMITTALHISTORYINFOCATEGORY: Info Category");


      headblk.addField("STATUS").
      setSize(20).
      setReadOnly().    // Bug Id 78792
      setMaxLength(400).
      setLabel("DOCUMENTTRANSMITTALHISTORY: Status");

      headblk.addField("NOTE").
      setSize(20).
      setMaxLength(400).
      setLabel("DOCUMENTTRANSMITTALHISTORYSTATUSNOTE: Note");

      headblk.addField("CREATED_BY").
      setSize(20).
      setMaxLength(400).
      setReadOnly().
      setLabel("DOCUMENTTRANSMITTALHISTORYCREATEDBY: Created By");
      
      
      
      headblk.addField("CREATED_DATE","Date").
      setSize(20).
      setMaxLength(400).
      setReadOnly().
      setLabel("DOCUMENTTRANSMITTALHISTORYCREATEDDATE: Created Date");

      
      headblk.setView("DOC_TRANSMITTAL_HISTORY");
      headblk.defineCommand("DOC_TRANSMITTAL_HISTORY_API","New__"); // Bug Id 78792 

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      headbar.defineCommand(headbar.NEWROW,"newRow1");
      headbar.addCustomCommand("gotoTransmittalInfo",mgr.translate("DOCUMENTTRANSMITTALHISTORYTRANSINFO: Transmittal Info..."));

      headbar.enableMultirowAction();
      
      headtbl = mgr.newASPTable(headblk);                            
      headtbl.setTitle(mgr.translate("DOCMAWDOCUMENTTRANSMITTALHISTORYOVWDOCCLAFORTRANSMIT: Overview - Document Classes for Transmittals"));
      headtbl.enableRowSelect();
      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

   }//end of predefine


    public void  validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");


      if(val.equals("TRANSMITTAL_ID"))
      {
         trans.clear();
         cmd = trans.addCustomFunction("TRANSMITTAL", "DOCUMENT_TRANSMITTAL_API.Get_Transmittal_Description", "TRANSMITTAL_DESCRIPTION");
         cmd.addParameter("TRANSMITTAL_ID");
         trans = mgr.validate(trans);

         String projectDesc         = trans.getValue("TRANSMITTAL/DATA/TRANSMITTAL_DESCRIPTION");
         StringBuffer response = new StringBuffer("");
         response.append(projectDesc);
         response.append("^");
         mgr.responseWrite(response.toString());
      }
     
      mgr.endResponse();
    }



    public void run()
   {
      ASPManager mgr = getASPManager();
      ctx     = mgr.getASPContext();
      fmt     = mgr.newASPHTMLFormatter();

      trans = mgr.newASPTransactionBuffer();
      sHistoryMode = ctx.readValue("HISTORY_MODE","6"); //Bug Id 75082

      if (mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if (!mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")))
         validate();
      else if ("TRUE".equals(mgr.readValue("MODE_CHANGED")))
			historyModeChanged();

        adjust();
      ctx.writeValue("HISTORY_MODE",sHistoryMode);
    }


     public void  adjust()
   {
      ASPManager mgr = getASPManager();
      // do any adjustments
      //Bug Id 75082, start
      if (headset.countRows() <=0 && !"TRUE".equals(mgr.readValue("MODE_CHANGED")))
         headlay.setLayoutMode(headlay.FIND_LAYOUT);
      //Bug Id 75082, end

     }

   public void  historyModeChanged()
	{
		ASPManager mgr = getASPManager();

		sHistoryMode = mgr.readValue("HISTORY_MODE");
		okFind();
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
    //===============================================================
    //  HTML
    //===============================================================
    protected String getDescription()
    {
       //Bug Id 68742, start
       if (headlay.isMultirowLayout())
          return "DOCMAWDOCUMENTTRANSMITTALHISTORYOVWDOCTRANSMITHIS: Overview - Document Transmittal History";
       else
          return "DOCMAWDOCUMENTTRANSMITTALHISTORYDOCTRANSMITHIS: Document Transmittal History";
       //Bug Id 68742, end
    }

    protected String getTitle()
    {
       return getDescription(); //Bug Id 68742
    }



   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      //appendToHTML(headlay.show());

      ctx.writeValue("HISTORY_MODE", "6"); //Bug Id 75082

      appendToHTML("<input type=\"hidden\" name=\"MODE_CHANGED\" value=\"FALSE\">\n");

      
      

      //headlay.getLayoutMode()

      if (headlay.isSingleLayout() || headlay.isMultirowLayout()) {
         appendToHTML(headbar.showBar());
         appendToHTML("<table cellpadding=\"10\" cellspacing=\"0\" border=\"0\" width=\"");
		   appendToHTML("\">\n");
		   appendToHTML("<tr><td>");// row 1
		   appendToHTML(fmt.drawRadio(mgr.translate("DOCUMENTTRANSMITTALHISTORYMODECREATED: Transmittal Created"), "HISTORY_MODE", "1", "1".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
		   appendToHTML("</td><td>");
		   appendToHTML(fmt.drawRadio(mgr.translate("DOCUMENTTRANSMITTALHISTORYMODESENTREC: Transmittal Sent or Received"), "HISTORY_MODE" , "2" , "2".equals(sHistoryMode),"OnClick=\"modeChanged()\""));
		   appendToHTML("</td><td>");
		   appendToHTML(fmt.drawRadio(mgr.translate("DOCUMENTTRANSMITTALHISTORYMODEDOCUMENT: document Added,Removed or Modified"), "HISTORY_MODE", "3", "3".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
		   appendToHTML("</td></tr><tr><td>");
		   appendToHTML(fmt.drawRadio(mgr.translate("DOCUMENTTRANSMITTALHISTORYMODEMETAMODI: Meta Data Modified"), "HISTORY_MODE", "4", "4".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
		   appendToHTML("</td><td>");
		   appendToHTML(fmt.drawRadio(mgr.translate("DOCUMENTTRANSMITTALHISTORYMODEMANUAL: Manual Note"), "HISTORY_MODE", "5", "5".equals(sHistoryMode),"OnClick=\"modeChanged()\""));
		   appendToHTML("</td><td>");
		   appendToHTML(fmt.drawRadio(mgr.translate("DOCUMENTTRANSMITTALHISTORYALL: All"), "HISTORY_MODE", "6", "6".equals(sHistoryMode), "OnClick=\"modeChanged()\""));
		   appendToHTML("</td></tr>");
		   appendToHTML("</table>\n");

         appendToHTML(headlay.generateDataPresentation());

         appendDirtyJavaScript("function modeChanged()\n");
		   appendDirtyJavaScript("{\n");
		   appendDirtyJavaScript("   document.form.MODE_CHANGED.value = \"TRUE\";\n");
		   appendDirtyJavaScript("   submit();\n");
		   appendDirtyJavaScript("}\n");

      }else
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
      else
      {
         String whereCondition;
         int nHistoryMode = new Integer(sHistoryMode).intValue();

         switch (nHistoryMode)
		  {
		      case 1:
			      whereCondition =  " DOC_TRANS_HISTORY_CAT_DB = 'TRANSMITTAL_CREATED'";
               break;
		      case 2 :
			      whereCondition =  " DOC_TRANS_HISTORY_CAT_DB IN ('TRANSMITTAL_SENT','TRANSMITTAL_RECEIVED')";
               break;
		      case 3 :
               whereCondition =  " DOC_TRANS_HISTORY_CAT_DB IN ('DOC_ADDED_TO_TRANS','DOC_REM_FROM_TRANS')";
               break;
		      case 4 :
               whereCondition =  " DOC_TRANS_HISTORY_CAT_DB = 'TRANS_METADATA_MODI'";
               break;
		      case 5 :
               whereCondition =  " DOC_TRANS_HISTORY_CAT_DB = 'MANUAL_NOTE'";
               break;
		      case 6 :
               whereCondition =  "";
               break;
		      default :
               whereCondition =  "";
		}
         if (!mgr.isEmpty(whereCondition)) {
            q.addWhereCondition(whereCondition);
         }

      }


      q.includeMeta("ALL");
      try{
         mgr.querySubmit(trans,headblk);
      }catch(Exception e)
      {
         
      }

      //Bug Id 75082, start
      if (headset.countRows() == 0 && !"TRUE".equals(mgr.readValue("MODE_CHANGED")))
      {
         mgr.showAlert(mgr.translate("DOCMAWDOCTRANSMITTALHISTORYOVWNODATA: No data found."));
         headset.clear();
      }
      //Bug Id 75082, end


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


   public void newRow1()
   {
      ASPManager mgr = getASPManager();
      trans.clear();
      cmd = trans.addEmptyCommand("HEAD","DOC_TRANSMITTAL_HISTORY_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }//end of newRow


}// end of class

