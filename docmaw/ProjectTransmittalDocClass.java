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
*  File        : ProjectTransmittalDocClass.java
*  MODIFIED    :
*    BAKALK   2006-12-21   Created.
*    BAKALK   2007-02-19   Corrrected wrong view name
*    AMNALK   2007-12-12   Bug Id 68742, Changed the name of the page title
*    AMNALK   2008-01-23   Bug Id 70702, Corrected the spelling errors of some API names
* ----------------------------------------------------------------------------
*/
package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class ProjectTransmittalDocClass extends ASPPageProvider
{
   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.ProjectTransmittalDocClass");


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
   public ProjectTransmittalDocClass(ASPManager mgr, String page_path)
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
      setDynamicLOV("PROJECT").
      setCustomValidation("PROJECT_ID","PROJECT_DESC").
      setLabel("PROJECTTRANSMITTALDOCCLASSPROJECTID: Project Id");
      

      headblk.addField("PROJECT_DESC").
      setFunction("PROJECT_API.Get_Description(:PROJECT_ID)").
      setSize(20).
      setMaxLength(40).
      setReadOnly().
      setUpperCase().
      setLabel("PROJECTTRANSMITTALDOCCLASSPROJDESC: Project Description");

      headblk.addField("DOC_CLASS").
      setSize(20).
      setMaxLength(12).
      setMandatory().
      setReadOnly().
      setInsertable().
      setUpperCase().
      setDynamicLOV("DOC_CLASS").
      setCustomValidation("DOC_CLASS","SDOCNAME").
      setLOVProperty("TITLE",mgr.translate("DOCMAWDOCTITLEOVWDOCCLASS1: List of Document Class")).
      setLabel("PROJECTTRANSMITTALDOCCLASSDOCCLASS: Doc Class");

      headblk.addField("SDOCNAME").
      setSize(20).
      setReadOnly().
      setFunction("DOC_CLASS_API.GET_NAME(:DOC_CLASS)").
      setLabel("PROJECTTRANSMITTALDOCCLASSSDOCNAME: Doc Class Desc");

      headblk.setView("PROJECT_DOCUMENT_CLASS");
      headblk.defineCommand("PROJECT_DOCUMENT_CLASS_API","New__,Remove__"); //Bug Id 70702: Corrected the name of API

      headset = headblk.getASPRowSet();

      headbar = mgr.newASPCommandBar(headblk);
      

       
      
      
      
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("PROJECTTRANSMITTALDOCCLASSTITLE: Document Classes per Project for Document Transmittals")); //Bug Id 68742
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
      else  if(val.equals("DOC_CLASS"))
      {
         trans.clear();
         cmd = trans.addCustomFunction("CLASSNAME", "DOC_CLASS_API.GET_NAME", "SDOCNAME");
         cmd.addParameter("DOC_CLASS");
         trans = mgr.validate(trans);

         String personName         = trans.getValue("CLASSNAME/DATA/SDOCNAME");
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
       return "PROJECTTRANSMITTALDOCCLASSTITLE: Document Classes per Project for Document Transmittals"; //Bug Id 68742
    }

    protected String getTitle()
    {
       return "PROJECTTRANSMITTALDOCCLASSTITLE: Document Classes per Project for Document Transmittals"; //Bug Id 68742
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
      cmd = trans.addEmptyCommand("HEAD","PROJECT_DOCUMENT_CLASS_API.New__",headblk); //Bug Id 70702: Corrected the name of API
      cmd.setOption("ACTION","PREPARE");
      trans = mgr.perform(trans);
      data = trans.getBuffer("HEAD/DATA");
      headset.addRow(data);
   }//end of newRow


}// end of class

