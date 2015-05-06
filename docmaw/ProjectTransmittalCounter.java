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
*  File        : ProjectTransmittalCounter.java
*  MODIFIED    :
*    BAKALK   2006-12-21   Created.
*    AMNALK   2007-12-12   Bug Id 68742, Changed the name of the page title
*    AMNALK   2010-09-23   Bug Id 93039, Modified preDefine() to make START_VALUE not updatable.
* ----------------------------------------------------------------------------
*/
package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ProjectTransmittalCounter extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.ProjectTransmittalCounter");


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
   public ProjectTransmittalCounter(ASPManager mgr, String page_path)
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

      headblk.addField("TRANSMITTAL_COUNTER_ID").
      setSize(20).
      setMaxLength(80).
      setMandatory().
      setReadOnly().
      setInsertable().
      setUpperCase().
      setLabel("PROJECTTRANSMITTALCOUNTERCOUNTERID: Couter Id");


      headblk.addField("START_VALUE","Number").
      setSize(20).
      setMandatory().
      // Bug Id 93039, start
      setReadOnly().
      setInsertable().
      // Bug Id 93039, end
      setLabel("PROJECTTRANSMITTALCOUNTERSTARTVALUE: Start value");


      headblk.addField("LENGTH_SHOWN","Number").
      setSize(20).
      setMaxLength(120).
      setLabel("PROJECTTRANSMITTALCOUNTERLENGTHSHOWN: Length Shown");

      headblk.addField("PREFIX").
      setSize(20).
      setMaxLength(40).
      setLabel("PROJECTTRANSMITTALCOUNTERPREFIX: Prefix");
      
      headblk.addField("SUFFIX").
      setSize(20).
      setMaxLength(40).
      setLabel("PROJECTTRANSMITTALCOUNTERSUFFIX: Suffix");

      headblk.addField("CURRENT_VALUE","Number").
      setSize(20).
      setMaxLength(10).
      setReadOnly().
      setLabel("PROJECTTRANSMITTALCOUNTERCURRENTVALUE: Current Value");


      headblk.addField("PROJECT_ID").
      setSize(20).
      setMaxLength(40).
      setUpperCase().
      setDynamicLOV("PROJECT").
      setCustomValidation("PROJECT_ID","PROJECT_DESC").
      setLabel("PROJECTTRANSMITTALCOUNTERPROJECTID: Project Id");

      headblk.addField("PROJECT_DESC").
      setFunction("PROJECT_API.Get_Description(:PROJECT_ID)").
      setSize(20).
      setMaxLength(40).
      setReadOnly().
      setUpperCase().
      setLabel("PROJECTTRANSMITTALCOUNTERDESC: Project Description");

      headblk.setView("TRANSMITTAL_COUNTER");
      headblk.defineCommand("TRANSMITTAL_COUNTER_API","New__,Modify__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      /*
      headbar.defineCommand(headbar.NEWROW,"newRow");
      headbar.defineCommand(headbar.SAVERETURN,"saveReturn");
      headbar.defineCommand(headbar.SAVENEW,"saveNew");
      headbar.defineCommand(headbar.CANCELNEW,"cancelNew");
      headbar.defineCommand(headbar.DUPLICATEROW,"duplicateRow");
      headbar.defineCommand(headbar.EDITROW, "editRow");*/

       
      
      
      
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWDOCTITLEOVWOVEDOCTI: Overview - Document Titles"));
      headtbl.enableRowSelect();
      headlay = headblk.getASPBlockLayout();
      headlay.setDialogColumns(2);
      headlay.setDefaultLayoutMode(headlay.MULTIROW_LAYOUT);

   }//end of predefine


    public void  validate()
   {
      ASPManager mgr = getASPManager();

      String val = mgr.readValue("VALIDATE");

     if(val.equals("PROJECT_ID"))
      {
         trans.clear();
         cmd = trans.addCustomFunction("PROJECT", "PROJECT_API.Get_Description", "PROJECT_DESC");
         cmd.addParameter("PROJECT_ID");
         trans = mgr.validate(trans);

         String projectDesc         = trans.getValue("PROJECT/DATA/PROJECT_DESC");
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
      // do any adjustments

     }
    //===============================================================
    //  HTML
    //===============================================================
    protected String getDescription()
    {
       return "PROJECTTRANSMITTALCOUNTERTITLE: Document Transmittal Counters"; //Bug Id 68742
    }

    protected String getTitle()
    {
       return "PROJECTTRANSMITTALCOUNTERTITLE: Document Transmittal Counters"; //Bug Id 68742
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
      cmd = trans.addEmptyCommand("HEAD","TRANSMITTAL_COUNTER_API.New__",headblk);
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }//end of newRow


}// end of class

