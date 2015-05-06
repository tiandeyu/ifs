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
*  File        : NewRevision.java 
*  Converted   : Bakalk using   ASP2JAVA Tool on 2001-03-15 
*  Created     : Using the ASP file NewRevision.asp
*  2003-08-29  InoSlk  Call ID 101731: Modified method clone().
*  2003-10-22  NiSilk  Call ID 107798: Added doc sheet
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;


public class NewRevision extends ASPPageProvider
{

   //===============================================================
   // Static constants 
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.NewRevision");


   //===============================================================
   // Instances created on page creation (immutable attributes) 
   //===============================================================
   private ASPHTMLFormatter fmt;
   private ASPContext ctx;
   private ASPBlock item1blk;
   private ASPRowSet item1set;
   private ASPTable item1tbl;
   private ASPBlockLayout item1lay;
   private ASPCommandBar item1bar;

   //===============================================================
   // Transient temporary variables (never cloned) 
   //===============================================================
   private ASPTransactionBuffer trans;
   private boolean currentStep;
   private String url;
   private String doc_class;
   private String dclass;
   private String doc_no;
   private String dno;
   private String doc_rev;
   private String drev;
   private String doc_sheet;
   private String dsheet;
   private ASPBuffer rev_select_buff;
   private String selected_value;
   private boolean structure_checkable;
   private boolean docsurvey_checkable;
   private boolean structure_val;
   private boolean copyapp_val;
   private String new_part_rev;
   private String jrnl_note;
   private String val;
   private ASPBuffer data;
   private String part_rev;
   private ASPCommand cmd;
   private String has_structure;
   private String has_app_route;
   private String ok;
   private ASPBuffer j;
   private ASPBuffer p;
   private String hasst;
   private String hasap;

   //===============================================================
   // Construction 
   //===============================================================
   public NewRevision(ASPManager mgr, String page_path)
   { 
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      currentStep   = false;
      url   = null;
      doc_class   = null;
      dclass   = null;
      doc_no   = null;
      dno   = null;
      doc_rev   = null;
      drev   = null;
      doc_sheet   = null;
      dsheet   = null;
      rev_select_buff   = null;
      selected_value   = null;
      structure_checkable   = false;
      docsurvey_checkable   = false;
      structure_val   = false;
      copyapp_val   = false;
      new_part_rev   = null;
      jrnl_note   = null;
      val   = null;
      data   = null;
      part_rev   = null;
      cmd   = null;
      has_structure   = null;
      has_app_route   = null;
      ok   = null;
      j   = null;
      p   = null;
      hasst   = null;
      hasap   = null;

      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      NewRevision page = (NewRevision)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;
      page.currentStep   = false;
      page.url   = null;
      page.doc_class   = null;
      page.dclass   = null;
      page.doc_no   = null;
      page.dno   = null;
      page.doc_rev   = null;
      page.drev   = null;
      page.doc_sheet   = null;
      page.dsheet   = null;
      page.rev_select_buff   = null;
      page.selected_value   = null;
      page.structure_checkable   = false;
      page.docsurvey_checkable   = false;
      page.structure_val   = false;
      page.copyapp_val   = false;
      page.new_part_rev   = null;
      page.jrnl_note   = null;
      page.val   = null;
      page.data   = null;
      page.part_rev   = null;
      page.cmd   = null;
      page.has_structure   = null;
      page.has_app_route   = null;
      page.ok   = null;
      page.j   = null;
      page.p   = null;
      page.hasst   = null;
      page.hasap   = null;
      
      // Cloning immutable attributes
      page.fmt = page.getASPHTMLFormatter();
      page.ctx = page.getASPContext();
      page.item1blk = page.getASPBlock(item1blk.getName());
      page.item1set = page.item1blk.getASPRowSet();
      page.item1tbl = page.getASPTable(item1tbl.getName());
      page.item1lay = page.item1blk.getASPBlockLayout();
      page.item1bar = page.item1blk.getASPCommandBar();

      return page;
   }

   public void run() 
   {
      ASPManager mgr = getASPManager();

   
      fmt   = mgr.newASPHTMLFormatter();
      trans = mgr.newASPTransactionBuffer();
      ctx   = mgr.getASPContext();   
   
      currentStep = ctx.readFlag("STEP_NO",false);      
      url       = ctx.readValue("SEND_URL");
   
      doc_class = mgr.readValue("DOC_CLASS",mgr.getQueryStringValue("DOC_CLASS"));
      dclass    = ctx.readValue("DOCCLASS");
	  
      //doc_class=="" ? doc_class = dclass  :  dclass = doc_class; 
      if("".equals(doc_class))
		  doc_class = dclass;
	  else
		   dclass = doc_class;
      doc_no    = mgr.readValue("DOC_NO",mgr.getQueryStringValue("DOC_NO"));
      dno       = ctx.readValue("DOCNO");
      //doc_no=="" ? doc_no = dno  :  dno = doc_no;    
      if("".equals(doc_no))
		  doc_no = dno;
	  else
		   dno = doc_no;
      doc_rev   = mgr.readValue("DOC_REV",mgr.getQueryStringValue("DOC_REV"));
      drev      = ctx.readValue("DREV");
      doc_sheet   = mgr.readValue("DOC_SHEET",mgr.getQueryStringValue("DOC_SHEET"));
      dsheet      = ctx.readValue("DSHEET");

      //doc_rev=="" ? doc_rev = drev  :  drev = doc_rev;       
      if(("".equals(doc_rev)) || ("".equals(doc_sheet)))
      {
		  doc_rev = drev;
        doc_sheet = dsheet;
      }
	  else
     {
		   drev = doc_rev; 
         dsheet = doc_sheet;
     }
      // for the select box
      rev_select_buff = ctx.readBuffer("REV_SELECT_BUFF");
      selected_value  = mgr.readValue("SELECTED_VALUE",mgr.getQueryStringValue("DOC_REV"));
      doc_rev           = fmt.populateListBox(rev_select_buff, selected_value);
   
      // ctx vars used for checkboxes
      structure_checkable = ctx.readFlag("STRUCT_CHECKABLE",false);
      docsurvey_checkable = ctx.readFlag("SURVEY_CHECKABLE",false);
      structure_val          = ctx.readFlag("STRUCT_VAL",false);
      copyapp_val        = ctx.readFlag("SURVEY_VAL",false);
   
      // other variables
      new_part_rev = mgr.readValue("NEW_PART_REV");   
      jrnl_note    = mgr.readValue("JOURNAL_NOTE");
   
   
      if(mgr.commandBarActivated())
         eval(mgr.commandBarFunction());
      else if( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();
      else if (mgr.dataTransfered())
         okFindITEM1();
      else if((!mgr.isEmpty(mgr.getQueryStringValue("DOC_NO"))) && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_SHEET"))) && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_CLASS"))) && (!mgr.isEmpty(mgr.getQueryStringValue("DOC_REV"))) && (!mgr.isEmpty(mgr.getQueryStringValue("SEND_URL"))))
         prepareDialog();    
      else if (mgr.buttonPressed("CANCEL"))
         Cancel();   
      else if (mgr.buttonPressed("FINISH"))
         finish();      
/*ASP2JAVA WARNING: Verify this condition.
 original condition 
      else if(mgr.readValue("SELECTED_VALUE")!="")*/

      else if ( !("".equals(mgr.readValue("SELECTED_VALUE"))) ) 
         assignFields();
   
      ctx.writeFlag("STEP_NO",currentStep);   
   
      //==================general ctx variables====================================
      ctx.writeValue("DOCCLASS",dclass);
      ctx.writeValue("DOCNO",dno);
      ctx.writeValue("DREV",drev);
      ctx.writeValue("SEND_URL",url);
   
      //===================== for the select box==================================
     // if (!mgr.isEmpty(rev_select_buff))   //countItems()
	  if(rev_select_buff!=null)
           ctx.writeBuffer("REV_SELECT_BUFF",rev_select_buff);
   
      //============================== Write ctx vars used for checkboxes========
      ctx.writeFlag("STRUCT_CHECKABLE",structure_checkable);
      ctx.writeFlag("SURVEY_CHECKABLE",docsurvey_checkable);
      ctx.writeFlag("STRUCT_VAL",structure_val);
      ctx.writeFlag("SURVEY_VAL",copyapp_val);
   
   }

//=============================================================================
//   Validation
//=============================================================================

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");
      mgr.showError("VALIDATE not implemented");
      mgr.endResponse();
   }

//=============================================================================

   public void  prepareDialog()
   {
      ASPManager mgr = getASPManager();

      // GET VALUES FOR COMBO BOX
      doc_rev   = mgr.getQueryStringValue("DOC_REV");
      doc_sheet   = mgr.getQueryStringValue("DOC_SHEET");
      url      = mgr.getQueryStringValue("SEND_URL");
      trans.clear();   
      //Bug ID 45944, inoslk, start
      ASPQuery q = trans.addQuery("EXISTING_REVS","DOC_ISSUE","DOC_REV,DOC_REV PR2","DOC_NO= ? AND DOC_CLASS= ? AND DOC_SHEET= ?","DOC_REV");
      q.addParameter("DOC_NO",doc_no);
      q.addParameter("DOC_CLASS",doc_class);
      q.addParameter("DOC_SHEET",doc_sheet);
      mgr.submit(trans);   
   
      data = ctx.getDbState().getBuffer("EXISTING_REVS");
      ctx.writeBuffer("REV_SELECT_BUFF",data);
      part_rev = fmt.populateListBox(data,selected_value);
   
      assignFields();  
   }


   public void  assignFields()
   {
      ASPManager mgr = getASPManager();

      trans.clear();
      cmd = trans.addCustomCommand("GETINFO","DOC_ISSUE_API.Get_Info");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("DOC_SHEET",doc_sheet);
      cmd.addParameter("DOC_REV",selected_value);   
      cmd.addParameter("HAS_STRUCTURE");
      cmd.addParameter("HAS_APP_ROUTE");
      cmd.addParameter("HAS_FILE");
      trans = mgr.perform(trans);
      has_structure = trans.getValue("GETINFO/DATA/HAS_STRUCTURE");
      has_app_route = trans.getValue("GETINFO/DATA/HAS_APP_ROUTE");
   
     // has_structure=="1" ? structure_val=structure_checkable=true :  structure_val=structure_checkable=false;
      if("1".equals(has_structure))
		  structure_val=structure_checkable=true;
	  else
		  structure_val=structure_checkable=false;
	  
	  //has_app_route=="1" ? copyapp_val=docsurvey_checkable=true :   copyapp_val=docsurvey_checkable=false;    
       if("1".equals(has_app_route))
		   copyapp_val=docsurvey_checkable=true;
	   else
		   copyapp_val=docsurvey_checkable=false; 
      trans.clear();
      cmd = trans.addCustomFunction("EXISTCHECK","DOC_REFERENCE_OBJECT_API.Exist_Doc_Reference","EXIST");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("DOC_SHEET",doc_sheet);
      cmd.addParameter("DOC_REV",selected_value);
      trans = mgr.perform(trans);
      ok = trans.getValue("EXISTCHECK/DATA/EXIST");
      trans.clear();
   
      data = trans.getBuffer("ITEM1/DATA");
      item1set.addRow(data);
   
      j = item1set.getRow();
      j.setFieldItem("JOURNAL_NOTE",jrnl_note);
      item1set.setRow(j);
   
      p = item1set.getRow();
      p.setFieldItem("NEW_PART_REV",new_part_rev);
      item1set.setRow(p);
   }

//=============================================================================
//   PUSH-BUTTON FUNCTIONS
//=============================================================================

   public void  finish()
   {
      ASPManager mgr = getASPManager();

/*ASP2JAVA WARNING: Verify this condition.
 original condition 
      if(mgr.readValue("CBCOPYSTRUCTURE") == "ON")    */

      if ( "ON".equals(mgr.readValue("CBCOPYSTRUCTURE")) )     
         hasst ="1";
      else 
         hasst ="0";
/*ASP2JAVA WARNING: Verify this condition.
 original condition 
      if(mgr.readValue("CBCOPYDOCUMENTSURVEY") == "ON")   */

      if ( "ON".equals(mgr.readValue("CBCOPYDOCUMENTSURVEY")) )    
         hasap ="1";
      else 
         hasap ="0";          
   
      trans.clear();
      cmd = trans.addCustomCommand("NEWREV2","DOC_ISSUE_API.NEW_REVISION2__");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("NEW_PART_REV",mgr.readValue("NEW_PART_REV"));   
      cmd.addParameter("OLD_PART_REV",selected_value);
      cmd.addParameter("JOURNAL_NOTE",mgr.readValue("JOURNAL_NOTE"));
      trans = mgr.perform(trans);
      trans.clear();           
   
      cmd = trans.addCustomCommand("NEWREV2","DOC_ISSUE_API.Copy_Info");
      cmd.addParameter("DOC_CLASS",doc_class);
      cmd.addParameter("DOC_NO",doc_no);
      cmd.addParameter("NEW_PART_REV",mgr.readValue("NEW_PART_REV"));   
      cmd.addParameter("OLD_PART_REV",selected_value);
      cmd.addParameter("HAS_STRUCTURE",hasst);
      cmd.addParameter("HAS_APP_ROUTE",hasap);
      cmd.addParameter("HAS_FILE","0");
      trans = mgr.perform(trans); 
   
      if  ( url.indexOf("DocIssueGeneral.page") != -1 ) 
         mgr.redirectTo(url+"?DOC_NO="+mgr.URLEncode(doc_no)+"&DOC_SHEET="+mgr.URLEncode(doc_sheet)+"&DOC_CLASS="+mgr.URLEncode(doc_class)+"&DOC_REV="+mgr.URLEncode(mgr.readValue("DOC_REV")));               
      else
         mgr.redirectTo(url+"?DOC_NO="+mgr.URLEncode(doc_no)+"&DOC_SHEET="+mgr.URLDecode(doc_sheet)+"&DOC_CLASS="+mgr.URLEncode(doc_class)+"&DOC_REV="+mgr.URLEncode(mgr.readValue("NEW_PART_REV")));               
   }


   public void  Cancel()
   {
      ASPManager mgr = getASPManager();

     mgr.redirectTo(url+"?DOC_NO="+mgr.URLEncode(doc_no)+"&DOC_SHEET="+mgr.URLEncode(doc_sheet)+"&DOC_CLASS="+mgr.URLEncode(doc_class)+"&DOC_REV="+mgr.URLEncode(selected_value));                  
   }


   public void  preDefine()
   {
      ASPManager mgr = getASPManager();

   
      // *********************************** DLG 1 ***********************************
      item1blk = mgr.newASPBlock("ITEM1");
   
      item1blk.addField("OBJID").
               setHidden();
   
      item1blk.addField("OBJVERSION").
               setHidden();
   
      item1blk.addField( "DOC_CLASS" ).
               setHidden().
               setFunction("''");
   
      item1blk.addField( "DOC_NO" ).
               setHidden().      
               setFunction("''");
      
      item1blk.addField("DOC_SHEET").
               setSize(20).
               setMaxLength(10).
               setReadOnly().
               setUpperCase().
               setDynamicLOV("DOC_ISSUE_LOV1").
               setLabel("DOCMAWNEWREVISIONNEWSHEET: Doc Sheet");
   
   
      item1blk.addField("DOC_REV").
               setSize(7).
               setMandatory().            
               setLabel("DOCMAWNEWREVISIONSNEWPARTREV: Revision").            
               setUpperCase().      
               setHidden().      
               setFunction("''");
   
      item1blk.addField( "HAS_STRUCTURE" ).
               setHidden().      
               setFunction("''");
   
      item1blk.addField( "HAS_APP_ROUTE" ).
               setHidden().      
               setFunction("''");
   
       item1blk.addField( "HAS_FILE" ).
               setHidden().      
               setFunction("''");
   
       item1blk.addField( "EXIST" ).
               setHidden().      
               setFunction("''");
   
   
      item1blk.addField("PART_NO").
               setHidden().
               setFunction("''");
   
      item1blk.addField("NEW_PART_REV").
               setSize(7).
               setMandatory().
               setLabel("DOCMAWNEWREVISIONSNEWPARTREV: Revision").
               setFunction("''").
               setUpperCase();
   
      item1blk.addField("OLD_PART_REV").
               setHidden().
               setFunction("''");
   
   
      item1blk.addField("JOURNAL_NOTE").
               setSize(48).
               setHeight(3).
               setMandatory().
               setLabel("DOCMAWNEWREVISIONMLSNEWPARTREVTEXT: Revision Text").
               setFunction("''");
   
      item1blk.setView("DOC_ISSUE");
      item1blk.defineCommand("ENG_PART_REVISION_API","New__,Modify__,Remove__");
      item1blk.setTitle("Create New revision");
   
      item1bar = mgr.newASPCommandBar(item1blk);
   
      item1set = item1blk.getASPRowSet();
   
      item1tbl = mgr.newASPTable(item1blk);
   
      item1lay = item1blk.getASPBlockLayout();
      item1lay.setDefaultLayoutMode(item1lay.CUSTOM_LAYOUT);
      item1lay.setEditable();
      item1lay.setDataSpan("JOURNAL_NOTE",5);  
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "DOCMAWNEWREVISIONTITLE: Create New Revision";
   }

   protected String getTitle()
   {
      return "DOCMAWNEWREVISIONTITLE: Create New Revision";
   }

   protected AutoString getContents() throws FndException
   { 
      AutoString out = getOutputStream();
      out.clear();
      ASPManager mgr = getASPManager();
      out.append("<html>\n");
      out.append("<head>\n");
      out.append(mgr.generateHeadTag("DOCMAWNEWREVISIONTITLE: Create New Revision"));
      out.append("</head>\n");
      out.append("<body ");
      out.append(mgr.generateBodyTag());
      out.append(">\n");
      out.append("<form ");
      out.append(mgr.generateFormTag());
      out.append(">  \n");
      out.append("   <input type=\"hidden\" name=\"SELECTED_VALUE\" value=\"");
      out.append(selected_value);
      out.append("\">\n");
      out.append("   <input type=\"hidden\" name=\"NP_REV\" value=\"");
      out.append(new_part_rev);
      out.append("\">   \n");
      out.append("   <input type=\"hidden\" name=\"JNOTE\" value=\"");
      out.append(jrnl_note);
      out.append("\">\n");
      out.append(mgr.startPresentation("DOCMAWNEWREVISIONTITLE: Create New Revision"));
      out.append("      <table id=\"HeadStep1\" border=\"0\" class=\"BlockLayoutTable\" width=\"727\">\n");
      out.append("      <tr>\n");
      out.append("         <td width=\"150\" align=\"center\"><hr size=\"1\" width=\"140\"></td>\n");
      out.append("         <td width=\"60\">");
      out.append(fmt.drawWriteLabel("DOCMAWNEWREVISIONNEW: New"));
      out.append("</td>\n");
      out.append("         <td align=\"center\"><hr size=\"1\" width=\"497\"></td>\n");
      out.append("      </tr>\n");
      out.append("      </table>\n");
      out.append(item1lay.show());
      out.append("      <table id=\"Step1Dlg\" border=\"0\" class=\"BlockLayoutTable\" width=\"727\">\n");
      out.append("      <tr>\n");
      out.append("         <td width=\"150\" align=\"center\"><hr size=\"1\" width=\"140\"></td>\n");
      out.append("         <td width=\"60\">");
      out.append(fmt.drawWriteLabel("DOCMAWNEWREVISIONFROMOLD: From Old"));
      out.append("</td>\n");
      out.append("         <td align=\"center\"><hr size=\"1\" width=\"497\"></td>\n");
      out.append("      </tr>\n");
      out.append("      <tr>\n");
      out.append("         <td>");
      out.append(fmt.drawWriteLabel("DOCMAWNEWREVISIONREVSON: Revision"));
      out.append("</td>\n");
      out.append("         <td>");
      out.append(fmt.drawSelect("DOC_REV_OLD", ctx.readBuffer("REV_SELECT_BUFF"), selected_value,"OnChange=updateAllValues()"));
      out.append("</td>\n");
      out.append("      </tr>\n");
      out.append("      <tr>\n");
      out.append("         <td>");
      out.append(fmt.drawWriteLabel("DOCMAWNEWREVISIONCPSTR: Copy Structure"));
      out.append("</td>\n");
      out.append("         <td>");
      out.append(fmt.drawCheckbox("CBCOPYSTRUCTURE","ON",structure_val,"OnClick=structureCheck()"));
      out.append("</td>\n");
      out.append("      </tr>\n");
      out.append("      <tr>\n");
      out.append("         <td>");
      out.append(fmt.drawWriteLabel("DOCMAWNEWREVISIONCPAPPROUTE: Copy Approval Routing"));
      out.append("</td>\n");
      out.append("         <td>");
      out.append(fmt.drawCheckbox("CBCOPYDOCUMENTSURVEY","ON",copyapp_val,"OnClick=surveyCheck()"));
      out.append("</td>\n");
      out.append("         <td align=\"right\">");
      out.append(fmt.drawSubmit("CANCEL",mgr.translate("DOCMAWNEWREVISIONCANCELL: Cancel"),""));
      out.append("&nbsp;&nbsp;                           \n");
      out.append(fmt.drawSubmit("FINISH",mgr.translate("DOCMAWNEWREVISIONFINISHH: Finish"),""));
      out.append("&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
      out.append("      </tr>\n");
      out.append("      </table>\n");
	  out.append(mgr.endPresentation());
	  out.append("</form>\n");
      out.append("</body>\n");
      out.append("</html>");
      appendDirtyJavaScript("//=============================================================================\n");
      appendDirtyJavaScript("//   CLIENT FUNCTIONS\n");
      appendDirtyJavaScript("//=============================================================================\n");
      appendDirtyJavaScript("function structureCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if('");
      appendDirtyJavaScript(structure_checkable);
      appendDirtyJavaScript("' == 'False')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYSTRUCTURE.checked=false;\n");
      appendDirtyJavaScript("      alert(\"This option is not available!\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function surveyCheck()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if ('");
      appendDirtyJavaScript(docsurvey_checkable);
      appendDirtyJavaScript("'=='False')\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.CBCOPYDOCUMENTSURVEY.checked=false;\n");
      appendDirtyJavaScript("      alert(\"This option is not available!\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function updateAllValues()\n");
      appendDirtyJavaScript("{     \n");
      appendDirtyJavaScript("   if (document.form.DOC_REV_OLD.value==\"\")\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.DOC_REV_OLD.value = document.form.SELECTED_VALUE.value;\n");
      appendDirtyJavaScript("      alert(\"You must select a revision!\");\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      document.form.SELECTED_VALUE.value = document.form.DOC_REV_OLD.value;            \n");
      appendDirtyJavaScript("      submit();\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      appendDirtyJavaScript("function toggleStyleDisplay(el)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   if(el.style.display!='none')\n");
      appendDirtyJavaScript("      el.style.display='none';\n");
      appendDirtyJavaScript("   else\n");
      appendDirtyJavaScript("      el.style.display='block';\n");
      appendDirtyJavaScript("   if(\"");
      appendDirtyJavaScript(currentStep);
      appendDirtyJavaScript("\"==\"False\" && el == cntITEM1)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if(HeadStep1.style.display!='none')\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         HeadStep1.style.display='none';\n");
      appendDirtyJavaScript("         Step1Dlg.style.display='none';\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("      else\n");
      appendDirtyJavaScript("      {\n");
      appendDirtyJavaScript("         HeadStep1.style.display='block';\n");
      appendDirtyJavaScript("         Step1Dlg.style.display='block';\n");
      appendDirtyJavaScript("      }\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   else if(el == cntITEM2)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("      if(Step2Dlg.style.display!='none')\n");
      appendDirtyJavaScript("         Step2Dlg.style.display='none';\n");
      appendDirtyJavaScript("      else\n");
      appendDirtyJavaScript("         Step2Dlg.style.display='block';\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");
      return out;
   }
   
   protected void okFindITEM1(){ // this method was not implemented in asp file 
                                 
   	}

}//end of class
