
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
*  File		ReportArchiveSettings.jave.
*  discription  it facilitate the user to set the report parameters through a diolog.
*  2006-10-12 	KARALK	CREATED.
*  2008-03-18   VIRALK bug id 61529 Small additions to the Interface. 
*  ------------------------------------------------------------------------------
*/



package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.Vector;
import java.lang.reflect.*;
import ifs.docmaw.DocmawUtil;


public class ReportArchiveSettings extends ASPPageProvider
{
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.ReportArchiveSettings");

   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPContext       ctx;
   private ASPBlock headblk;
   private ASPField f;
   private ASPRowSet      headset;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPCommand cmd;
   private ASPQuery q;

   private ASPHTMLFormatter fmt;
   private String doc_class;
   private String doc_no;
   private String sSetPreObs;
   private String sDelObsRev;
   private String sNoDelParChg;
   private boolean bSetPreObs;
   private boolean bDelObsRev;
   private boolean bNoDelParChg;
   private boolean bsaved;
   private boolean bvisited;
   private boolean bdefault;


   public ReportArchiveSettings(ASPManager mgr, String page_path)
   {
      super(mgr, page_path);
   }


   public void run() throws FndException
   {
      ASPManager mgr = getASPManager();
      fmt   = mgr.newASPHTMLFormatter();
      ctx     = mgr.getASPContext();

      doc_class = ctx.readValue("DOC_CLASS", "");
      doc_no = ctx.readValue("DOC_NO", "");

      if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS")))
         doc_class = mgr.readValue("DOC_CLASS");
      if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_NO")))
         doc_no = mgr.readValue("DOC_NO");
      
      
      if (!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS")))
          okFind();

      
      sSetPreObs = headset.getRow().getValue("REP_ARCH_SET_PREV_OBS");
      sDelObsRev = headset.getRow().getValue("REP_ARCH_DEL_OBS_REVS");
      sNoDelParChg = headset.getRow().getValue("REP_ARCH_NO_DEL_ON_PAR_CHG");

      if ("TRUE".equals(mgr.readValue("GETDEFAULTVALUES")))	
      {
          getDefaultValues();
     // bug id 61529 start.
          bdefault=true;
     // bug id 61529 end.
      }
      setValues();

      if ("TRUE".equals(mgr.readValue("SAVE1")))	
      {
          Save();
     // bug id 61529 start.
	  bsaved = true;
     // bug id 61529 end.
      }

       
      ctx.writeValue("DOC_CLASS", doc_class);
      ctx.writeValue("DOC_NO", doc_no);
      
   }

   public void okFind()
   {
       ASPManager mgr = getASPManager();

       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();       
       q = trans.addQuery(headblk);
       q.addWhereCondition("DOC_CLASS = ? AND DOC_NO = ?");
       q.addParameter("DOC_CLASS", doc_class);
       q.addParameter("DOC_NO", doc_no);
       q.includeMeta("ALL");
       mgr.querySubmit(trans, headblk);
   }

   public void setValues()// set the values to check boxes
   {
       if("Y".equals(sSetPreObs))
       {
           bSetPreObs = true;
       }
       else
       {
           bSetPreObs = false;
       }

       if("Y".equals(sDelObsRev))
       {
           bDelObsRev = true;
       }
       else
       {
           bDelObsRev = false;
       }

       if("Y".equals(sNoDelParChg))
       {
           bNoDelParChg = true;
       }
       else
       {
           bNoDelParChg = false;
       }
   }

   public void  getDefaultValues()   // Get the default values for the check box
   {
       ASPManager mgr = getASPManager();
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();       
       cmd = trans.addCustomFunction("DEFAULTVALUE", "doc_class_default_api.get_default_value_", "DUMMY3");
       cmd.addParameter("DUMMY1",ctx.readValue("DOC_CLASS"));
       cmd.addParameter("DUMMY2","DocTitle");
       cmd.addParameter("DUMMY1","REP_ARCH_SET_PREV_OBSOLETE");
       trans = mgr.perform(trans);
       sSetPreObs = trans.getValue("DEFAULTVALUE/DATA/DUMMY3");

       trans.clear();
       cmd = trans.addCustomFunction("DEFAULTVALUE", "doc_class_default_api.get_default_value_", "DUMMY3");
       cmd.addParameter("DUMMY1",ctx.readValue("DOC_CLASS"));
       cmd.addParameter("DUMMY2","DocTitle");
       cmd.addParameter("DUMMY1","REP_ARCH_DEL_OBSOLETE_REVS");
       trans = mgr.perform(trans);
       sDelObsRev = trans.getValue("DEFAULTVALUE/DATA/DUMMY3");

       trans.clear();
       cmd = trans.addCustomFunction("DEFAULTVALUE", "doc_class_default_api.get_default_value_", "DUMMY3");
       cmd.addParameter("DUMMY1",ctx.readValue("DOC_CLASS"));
       cmd.addParameter("DUMMY2","DocTitle");
       cmd.addParameter("DUMMY1","REP_ARCH_NO_DEL_ON_PARAM_CHG");

       trans = mgr.perform(trans);
       sNoDelParChg = trans.getValue("DEFAULTVALUE/DATA/DUMMY3");

   }

   public void  Save()
   {
       ASPManager mgr = getASPManager();
       
       StringBuffer Attr = new StringBuffer();
       
       if (!mgr.isEmpty(mgr.readValue("SETPREVIOUSOBS"))){ 
           if ("TRUE".equals(mgr.readValue("SETPREVIOUSOBS"))) 
           {
               bSetPreObs = true;
               DocmawUtil.addToAttribute(Attr,"REP_ARCH_SET_PREV_OBS","Y");
           }
           else
           {
               bSetPreObs = false;
               DocmawUtil.addToAttribute(Attr,"REP_ARCH_SET_PREV_OBS","N");
           }
       }

       if (!mgr.isEmpty(mgr.readValue("DELOBSOLETEREV"))){ 
           if ("TRUE".equals(mgr.readValue("DELOBSOLETEREV"))) 
           {
               bDelObsRev = true;
               DocmawUtil.addToAttribute(Attr,"REP_ARCH_DEL_OBS_REVS","Y");
           }
           else
           {
               bDelObsRev = false;
               DocmawUtil.addToAttribute(Attr,"REP_ARCH_DEL_OBS_REVS","N");
           }
       }

       if (!mgr.isEmpty(mgr.readValue("NODELONPARACHG"))){ 
           if ("TRUE".equals(mgr.readValue("NODELONPARACHG"))) 
           {
               bNoDelParChg = true;
               DocmawUtil.addToAttribute(Attr,"REP_ARCH_NO_DEL_ON_PAR_CHG","Y");
           }
           else
           {
               bNoDelParChg = false;
               DocmawUtil.addToAttribute(Attr,"REP_ARCH_NO_DEL_ON_PAR_CHG","N");
           } 
       }     
       ASPTransactionBuffer trans = mgr.newASPTransactionBuffer();       
       cmd = trans.addCustomCommand("REPARCHUPDATE", "doc_title_api.update_title");
       
       cmd.addParameter("DUMMY1",Attr.toString());
       cmd.addParameter("DUMMY2",ctx.readValue("DOC_CLASS"));
       cmd.addParameter("DUMMY1",ctx.readValue("DOC_NO"));
       trans = mgr.perform(trans);
       
   }

   protected String getDescription()
   {
      return getTitle();
   }


   protected String getTitle()
   {
      return "DOCMAWREPORTARCHIVESETTINGS: Report archive settings";
   }

   public void preDefine() throws FndException
   {
      ASPManager mgr = getASPManager();
      disableHeader();
      disableBar();
      disableNavigate();
      // bug id 61529 start.
      bvisited = true;
      // bug id 61529 end.
      

      headblk = mgr.newASPBlock("HEAD");

      headblk.addField("OBJID").
      setHidden();

      headblk.addField("OBJVERSION").
      setHidden();

      headblk.addField("DOC_CLASS").
      setHidden();

      headblk.addField("DOC_NO").
      setHidden();

      headblk.addField("REP_ARCH_SET_PREV_OBS").
      setHidden();

      headblk.addField("REP_ARCH_DEL_OBS_REVS").
      setHidden();
      
      headblk.addField("REP_ARCH_NO_DEL_ON_PAR_CHG").
      setHidden();

      headblk.addField("DUMMY1").
      setHidden().
      setFunction("''");  

      headblk.addField("DUMMY2").
      setHidden().
      setFunction("''");  

      headblk.addField("DUMMY3").
      setHidden().
      setFunction("''");  
    
      headblk.setView("DOC_TITLE");
      headblk.defineCommand("DOC_TITLE_API","New__,Modify__,Remove__");
      headset = headblk.getASPRowSet();
   }

   

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();

      appendToHTML("<input type=\"hidden\" name=\"GETDEFAULTVALUES\" value=\"FALSE\">");
      appendToHTML("<input type=\"hidden\" name=\"SAVE1\" value=\"FALSE\">");
      appendToHTML("<input type=\"hidden\" name=\"SETPREVIOUSOBS\" value=\"FALSE\">");
      appendToHTML("<input type=\"hidden\" name=\"DELOBSOLETEREV\" value=\"FALSE\">");
      appendToHTML("<input type=\"hidden\" name=\"NODELONPARACHG\" value=\"FALSE\">");
      
      appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
      appendToHTML("	<TR align=\"left\" vAlign=\"bottom\" height=\"30\" style=\"LEFT: 80px; \">\n");
      appendToHTML("       <td width=\"30\"></td>");  
      appendToHTML("       <td >"+fmt.drawCheckbox("SETPREOBS_CHECK","ON",bSetPreObs,"onClick=setValue()")+fmt.drawReadLabel("DOCMAWNEWREPORTARCHIVESETTINGSSETPREOBS: Set Previous Released Revision to Obsolete")+"</td>\n");
      appendToHTML("   </tr>\n");
      appendToHTML("   </table>\n"); 
      appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
      appendToHTML("	<TR vAlign=\"bottom\" height=\"30\" >\n");
      appendToHTML("       <td width=\"50\"></td>");  
      appendToHTML("       <td style=\"LEFT: 120px; \">"+fmt.drawCheckbox("DELOBSREV_CHECK","ON",bDelObsRev,"onClick=setValue()")+fmt.drawReadLabel("DOCMAWNEWREPORTARCHIVESETTINGSDELOBSREV: Delete Obsoleted Revision")+"</td>\n");
      appendToHTML("   </tr>\n");
      appendToHTML("   <tr>\n");
      appendToHTML("   </table>\n");

      appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
      appendToHTML("	<TR vAlign=\"bottom\" height=\"30\" >\n");
      appendToHTML("       <td width=\"80\"></td>");  
      appendToHTML("       <td style=\"LEFT: 120px; \">"+fmt.drawCheckbox("NODELONPARACHG_CHECK","ON",bNoDelParChg,"onClick=setValue()")+fmt.drawReadLabel("DOCMAWNEWREPORTARCHIVESETTINGSNODELONPARACHG: Do not Delete on Parameter Change")+"</td>\n");
      appendToHTML("   </tr>\n");
      
      appendToHTML("   </table>\n");
      //start Bug id 61529 start.
      appendToHTML("   <table align=\"center\" width=\"80%\" BORDER=\"0\" CELLPADDING=\"0\"> \n");
      appendToHTML("       <tr> \n");
      appendToHTML("       <td height = \"30\"> </TR>");
      appendToHTML("       <tr bgcolor = \"dcdcdc\"> \n");
      if (!bsaved)       
      {
	  appendToHTML("       <td width=\"100%\" align =\"center\">"+fmt.drawReadOnlyTextField("txtmsg","","")+"</td>\n");
      }
      else
      {      
	  appendToHTML("       <td width=\"100%\" align =\"center\">"+fmt.drawReadOnlyTextField("txtmsg",mgr.translate("DOCMAWNEWREPORTARCHIVESETTINGSCHANGESSAVED: Changes saved successfully "),"",30)+"</td>\n");

      }
      appendToHTML("       </tr>");
      appendToHTML("   </table>");
      //start Bug id 61529 end.


      appendToHTML(" <table border=\"0\" width=\"100%\" align=\"center\">\n");
      appendToHTML("       <td width=\"120\"></td>");  
      appendToHTML("       <td>"+fmt.drawButton("GETDEFAULT",mgr.translate("DOCMAWNEWREPORTARCHIVESETTINGSGETDEFAULT:  Get Default "),"onClick=GetValues()")+"</td>\n");
      appendToHTML("       <td width=\"20\"></td>");  
      appendToHTML("       <td>"+fmt.drawButton("SAVE",mgr.translate("DOCMAWNEWREPORTARCHIVESETTINGSSAVE:  Save "),"onClick=Save()")+"</td>\n");
      appendToHTML("       <td width=\"20\"></td>");  
      appendToHTML("       <td>"+fmt.drawButton("CLOSE",mgr.translate("DOCMAWNEWREPORTARCHIVESETTINGSCLOSE: Close"),"OnClick=Close()")+"</td>\n");
      appendToHTML("       <td width=\"140\"></td>");  
      appendToHTML("   </tr>\n");
      appendToHTML("   </table>\n");

      appendToHTML("   <script LANGUAGE=\"JavaScript\">\n");
      appendToHTML("      reSetValue();\n\n");
      // bug id 61529 strat. 
      if (bvisited){ 
          appendToHTML("         document.form.SAVE.disabled = true \n");  
      }

      if (bdefault){
      
          appendToHTML("         document.form.SAVE.disabled = false \n");
      }
      
      else if (bsaved) 
          appendToHTML("         document.form.SAVE.disabled = true \n");
      // bug id 61529 end. 
      appendToHTML("      function setValue()\n");
      appendToHTML("      { \n");
     // bug id 61529 start.
      appendToHTML("         if (document.form.DELOBSREV_CHECK.checked==true && document.form.SETPREOBS_CHECK.checked==true)\n");
      appendToHTML("         { \n");
      appendToHTML("            document.form.NODELONPARACHG_CHECK.style.visibility = \"visible\";\n");
      appendToHTML("         } \n");
      appendToHTML("         else \n");
      appendToHTML("         { \n");
      appendToHTML("            document.form.NODELONPARACHG_CHECK.style.visibility = \"hidden\";\n");
      appendToHTML("         } \n");
      // bug id 61529 end.
      appendToHTML("         if (document.form.SETPREOBS_CHECK.checked==true)\n");
      appendToHTML("         { \n");
      appendToHTML("            document.form.DELOBSREV_CHECK.style.visibility = \"visible\";\n");
      appendToHTML("         } \n");
      appendToHTML("         else \n");
      appendToHTML("         { \n");
      appendToHTML("            document.form.DELOBSREV_CHECK.style.visibility = \"hidden\";\n");
      appendToHTML("         } \n");
      
      // bug id 61529 start.
      appendToHTML("         document.form.SAVE.disabled = false \n");
      appendToHTML("         document.form.txtmsg.value = \"\" \n");
      // bug id 61529 end.
      appendToHTML("      }\n");


      // bug id 61529 strat.
      appendToHTML("      function reSetValue()\n");
      appendToHTML("      { \n");
      
      appendToHTML("         if (document.form.DELOBSREV_CHECK.checked==true && document.form.SETPREOBS_CHECK.checked==true)\n");
      appendToHTML("         { \n");
      appendToHTML("            document.form.NODELONPARACHG_CHECK.style.visibility = \"visible\";\n");
      appendToHTML("         } \n");
      appendToHTML("         else \n");
      appendToHTML("         { \n");
      appendToHTML("            document.form.NODELONPARACHG_CHECK.style.visibility = \"hidden\";\n");
      appendToHTML("         } \n");
      appendToHTML("         if (document.form.SETPREOBS_CHECK.checked==true)\n");
      appendToHTML("         { \n");
      appendToHTML("            document.form.DELOBSREV_CHECK.style.visibility = \"visible\";\n");
      appendToHTML("         } \n");
      appendToHTML("         else \n");
      appendToHTML("         { \n");
      appendToHTML("            document.form.DELOBSREV_CHECK.style.visibility = \"hidden\";\n");
      appendToHTML("         } \n");
      appendToHTML("      }\n");
      // bug id 61529 end.

      appendToHTML("      function Close()\n");
      appendToHTML("      { \n");
      appendToHTML("        window.close();\n");
      appendToHTML("      }\n");
      appendToHTML("      function GetValues()\n");
      appendToHTML("      { \n");
      appendToHTML("        document.form.GETDEFAULTVALUES.value=\"TRUE\"\n");
      appendToHTML("        submit() \n");
      appendToHTML("      }\n");
      appendToHTML("      function Save()\n");
      appendToHTML("      { \n");
      appendToHTML("        document.form.SAVE1.value=\"TRUE\"\n");  
      appendToHTML("        if (document.form.SETPREOBS_CHECK.checked==true)\n");
      appendToHTML("           document.form.SETPREVIOUSOBS.value=\"TRUE\"\n");
      appendToHTML("        else \n");
      appendToHTML("           document.form.SETPREVIOUSOBS.value=\"FALSE\"\n");
      appendToHTML("        if (document.form.DELOBSREV_CHECK.checked==true)\n");
      appendToHTML("           document.form.DELOBSOLETEREV.value=\"TRUE\"\n");
      appendToHTML("        else \n");
      appendToHTML("           document.form.DELOBSOLETEREV.value=\"FALSE\"\n");
      appendToHTML("        if (document.form.NODELONPARACHG_CHECK.checked==true)\n");
      appendToHTML("           document.form.NODELONPARACHG.value=\"TRUE\"\n");
      appendToHTML("        else \n");
      appendToHTML("           document.form.NODELONPARACHG.value=\"FALSE\"\n");
      appendToHTML("        submit() \n");     
      appendToHTML("      }\n");
      appendToHTML("   </script>\n");
   }
}
