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
*  File        : ProjectTransmittalReceivers.java
*  MODIFIED    :
*    BAKALK   2006-12-21   Created.
*    BAKALK   2007-02-19   Corrrected wrong view name
*    AMNALK   2007-12-12   Bug Id 68742, Changed the names of the page title
*    AMNALK   2008-01-23   Bug Id 70702, Corrected the spelling errors of some API & VIEW names
*    VIRALK   2008-03-03   Bug Id 69651 Replaced view PERSON_INFO with PERSON_INFO_LOV.
*    AMCHLK   2010-04-30   Bug Id 89680 Replaced view PERSON_INFO_LOV with PERSON_INFO_PUBLIC_LOV.
* ----------------------------------------------------------------------------
*/
package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ProjectTransmittalReceivers extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.ProjectTransmittalReceivers");


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
   public ProjectTransmittalReceivers(ASPManager mgr, String page_path)
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

      headblk.addField("PROJECT_ID").
      setSize(20).
      setMaxLength(40).
      setReadOnly().
      setInsertable().
      setUpperCase().
      setCustomValidation("PROJECT_ID","PROJECT_DESC").
      setDynamicLOV("PROJECT").
      setLabel("PROJECTTRANSMITTALRECEIVERSPROJECTID: Project Id");
      

      headblk.addField("PROJECT_DESC").
      setFunction("PROJECT_API.Get_Description(:PROJECT_ID)").
      setSize(20).
      setMaxLength(40).
      setReadOnly().
      setUpperCase().
      setLabel("PROJECTTRANSMITTALRECEIVERSPROJDESC: Project Description");

      
      headblk.addField("PERSON_ID").
      setSize(20).
      setMaxLength(80).
      setReadOnly().
      setInsertable().
      setUpperCase().
      setCustomValidation("PERSON_ID","PERSON_NAME").
      setDynamicLOV("PERSON_INFO_PUBLIC_LOV"). // Bug Id 89680
      setLabel("PROJECTTRANSMITTALRECEIVERSPERSONID: Person Id");

      

      headblk.addField("PERSON_NAME").
      setFunction("PERSON_INFO_API.Get_Name(:PERSON_ID)").
      setSize(20).
      setMaxLength(40).
      setReadOnly().
      setUpperCase().
      setLabel("PROJECTTRANSMITTALRECEIVERSPERSONNAME: Person Name");


      

      headblk.setView("PROJ_TRANSMITTAL_RECEIVER"); //Bug Id 70702, Corrected the name of the VIEW
      headblk.defineCommand("PROJ_TRANSMITTAL_RECEIVER_API","New__,Remove__");

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
            
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("PROJECTTRANSMITTALRECEIVERSTITLE: Document Transmittal Receivers")); //Bug Id 68742
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
         cmd = trans.addCustomFunction("PROJECTN", "PROJECT_API.Get_Description", "PROJECT_DESC");
         cmd.addParameter("PROJECT_ID");
         trans = mgr.validate(trans);

         String projectDesc         = trans.getValue("PROJECTN/DATA/PROJECT_DESC");
         StringBuffer response = new StringBuffer("");
         response.append(projectDesc);
         response.append("^");
         mgr.responseWrite(response.toString());
      }
      else  if(val.equals("PERSON_ID"))
      {
         trans.clear();
         cmd = trans.addCustomFunction("PERSONN", "PERSON_INFO_API.Get_Name", "PERSON_NAME");
         cmd.addParameter("PERSON_ID");
         trans = mgr.validate(trans);

         String personName         = trans.getValue("PERSONN/DATA/PERSON_NAME");
         StringBuffer response = new StringBuffer("");
         response.append(personName);
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
       return "PROJECTTRANSMITTALRECEIVERSTITLE: Document Transmittal Receivers"; //Bug Id 68742
    }

    protected String getTitle()
    {
       return "PROJECTTRANSMITTALRECEIVERSTITLE: Document Transmittal Receivers"; //Bug Id 68742
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
      cmd = trans.addEmptyCommand("HEAD","PROJ_TRANSMITTAL_RECEIVER_API.New__",headblk); //Bug Id 70702, Corrected the name of the API
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }//end of newRow


}// end of class

