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
*  File        : MandatorySearchFieldsConfigDlg.java
*  Modified    :
*      2008-03-19   SHTHLK  Bug Id 67105, Created file.      
*
* ----------------------------------------------------------------------------
*/


package ifs.docmaw;

import ifs.fnd.asp.*;
import ifs.fnd.buffer.*;
import ifs.fnd.service.*;
import ifs.fnd.*;
import java.util.*;


public class MandatorySearchFieldsConfigDlg extends ASPPageProvider
{

   //===============================================================
   // Static constants
   //===============================================================
   public static boolean DEBUG = Util.isDebugEnabled("ifs.docmaw.MandatorySearchFieldsConfigDlg");


   //===============================================================
   // Instances created on page creation (immutable attributes)
   //===============================================================
   private ASPHTMLFormatter fmt;   
   private ASPContext ctx;
   private ASPLog log;
   private ASPBlock headblk;
   private ASPRowSet headset;
   private ASPCommandBar headbar;
   private ASPTable headtbl;
   private ASPBlockLayout headlay;
   private ASPBlock itemblk;
   private ASPRowSet itemset;
   private ASPCommandBar itembar;
   private ASPTable itemtbl;
   private ASPBlockLayout itemlay;
   private ASPField f;

   //===============================================================
   // Transient temporary variables (never cloned)
   //===============================================================
   private ASPTransactionBuffer trans;
   private String val;
   private ASPBuffer buff;
   private ASPQuery q;
   private String searchURL;
   private ASPCommand cmd;
   private String pageURL;

   //===============================================================
   // Construction
   //===============================================================
   public MandatorySearchFieldsConfigDlg(ASPManager mgr, String page_path)
   {
      super(mgr,page_path);
   }


   protected void doReset() throws FndException
   {
      //Resetting mutable attributes
      trans   = null;
      pageURL = null;


      super.doReset();
   }

   public ASPPoolElement clone(Object obj) throws FndException
   {
      MandatorySearchFieldsConfigDlg page = (MandatorySearchFieldsConfigDlg)(super.clone(obj));

      // Initializing mutable attributes
      page.trans   = null;

      // Cloning immutable attributes
      page.ctx = page.getASPContext();
      page.log = page.getASPLog();
      page.headblk = page.getASPBlock(headblk.getName());
      page.headset = page.headblk.getASPRowSet();
      page.headbar = page.headblk.getASPCommandBar();
      page.headtbl = page.getASPTable(headtbl.getName());
      page.headlay = page.headblk.getASPBlockLayout();
      page.itemblk = page.getASPBlock(itemblk.getName());
      page.itemset = page.itemblk.getASPRowSet();
      page.itembar = page.itemblk.getASPCommandBar();
      page.itemtbl = page.getASPTable(itemtbl.getName());
      page.itemlay = page.itemblk.getASPBlockLayout();
      page.f = page.getASPField(f.getName());

      return page;
   }

   public void run()
   {
      ASPManager mgr = getASPManager();


      trans = mgr.newASPTransactionBuffer();
      ctx = mgr.getASPContext();
      fmt = mgr.newASPHTMLFormatter();
      pageURL = ctx.readValue("URL", "");

      if( mgr.commandBarActivated() )
         eval(mgr.commandBarFunction());
      else if( !mgr.isEmpty(mgr.getQueryStringValue("VALIDATE")) )
         validate();
      else if (!mgr.isEmpty(mgr.readValue("URL")))
	{ 
	  pageURL = mgr.readValue("URL");
	  populateData();  
        }
      else if (!mgr.isEmpty(mgr.readValue("SELECTED_FIELDS")))
	 saveData();      
      
      adjust();
      ctx.writeValue("URL", pageURL);
      
   }

//-----------------------------------------------------------------------------
//-----------------------------  VALIDATE FUNCTION  ---------------------------
//-----------------------------------------------------------------------------

   public void  validate()
   {
      ASPManager mgr = getASPManager();

      val = mgr.readValue("VALIDATE");
      mgr.showError("VALIDATE not implemented");
      mgr.endResponse();
   }

//-----------------------------------------------------------------------------
//-----------------------  CMDBAR CUSTOM FUNCTIONS  ---------------------------
//-----------------------------------------------------------------------------



//-----------------------------------------------------------------------------
//----------------------------  CMDBAR FUNCTIONS  -----------------------------
//-----------------------------------------------------------------------------

   public void saveData()
   {       
       ASPManager mgr = getASPManager();
       String selectedFields=mgr.readValue("SELECTED_FIELDS");

       headset.first();
       do
       {
	   headset.setValue("SELECTED","0");
       }
       while (headset.next()); 

       if ("^".equals(selectedFields))
	   selectedFields = "";
       else
       {
	   StringTokenizer st = new StringTokenizer(selectedFields, "^");
	   int no_fields = st.countTokens();
	   String current_field;
	   String sField;
	   for (int x = 0; x < no_fields; x++)
	   {
	       current_field = st.nextToken();
	       headset.first();
	       do
	       {
		    sField = headset.getValue("ASP_FIELD");
		    if (sField.equals(current_field))
		    {
			headset.setValue("SELECTED","1");
			break;
		    }
	       }
	       while (headset.next()); 
	   }
       }
	   
       trans.clear();
       cmd = trans.addCustomCommand("UPDATEDATA","DOCMAN_DEFAULT_API.Set_Default_Value_");
       cmd.addParameter("DUMMY1","DocIssue");
       cmd.addParameter("DUMMY2","DOC_ISSUE_MANDATORY_FIELDS");
       cmd.addParameter("DUMMY3",selectedFields);
       trans = mgr.perform(trans);
   }
   
   private void populateData()
   {
	  ASPManager mgr = getASPManager();
	    

          ASPPage profilePage = mgr.getProfilePage(pageURL);
	  ASPBlock blk = mgr.getASPBlockFromPage(profilePage,"HEAD");
          String sFieldList = blk.getFieldList();
	  
	  
	  StringTokenizer st = new StringTokenizer(sFieldList, ",");
	  int no_fields = st.countTokens();
	  String current_field;
	   for (int x = 0; x < no_fields; x++)
	   {
	      current_field = st.nextToken();
	      if (! profilePage.getASPField(current_field).isHidden()) 
	      {
		 ASPBuffer tmpbuff = getASPManager().newASPBuffer();
		 tmpbuff.addFieldItem("ASP_FIELD",current_field);
		 tmpbuff.addFieldItem("CLIENT_FIELD",profilePage.getASPField(current_field).getLabel());
		 tmpbuff.addFieldItem("SELECTED","0");
		 headset.addRow(tmpbuff);
	      }
	    }

	    trans.clear();
	    cmd = trans.addCustomFunction("MANDATORYFIELDS", "DOCMAN_DEFAULT_API.Get_Default_Value_", "DUMMY3");
	    cmd.addParameter("DUMMY1","DocIssue");
	    cmd.addParameter("DUMMY2","DOC_ISSUE_MANDATORY_FIELDS");
	    trans = mgr.perform(trans);
	    String sMandatoryFieldsList = trans.getValue("MANDATORYFIELDS/DATA/DUMMY3");
	    
	    String sMandatoryField;
	    String sField;

	    
	    if ((! mgr.isEmpty(sMandatoryFieldsList)) && stringIndex(sMandatoryFieldsList, "^") > 0) 
	    {
		StringTokenizer stm = new StringTokenizer(sMandatoryFieldsList, "^");
		while (stm.hasMoreTokens())
		{
		    sMandatoryField = stm.nextToken();
		    headset.first();
		    for (int x = 0; x < headset.countRows(); x++)
		    {
			sField = headset.getValue("ASP_FIELD");
			if (sField.equals(sMandatoryField))
			{
                            headset.setValue("SELECTED","1");
			    break;
			}
			headset.next();
		     }
		}
	    }
   }

   private int stringIndex(String mainString,String subString)
   {
	int a = mainString.length();
	int index = -1;
        
	for (int i = 0; i < a; i++)
	 if (mainString.startsWith(subString,i))
	 {
		index=i;
		break;
	 }
	 return index;
    }

   public void  preDefine()
   {
      ASPManager mgr = getASPManager();


      headblk = mgr.newASPBlock("HEAD");
      
      headblk.disableDocMan();

      headblk.addField("ASP_FIELD").
	  setFunction("''").
	  setHidden();

     headblk.addField("CLIENT_FIELD").
	   setFunction("''").
	   setHidden();

      headblk.addField("SELECTED").
	  setFunction("''").
	  setCheckBox("0,1").
	  setHidden();
	   

      headblk.addField("DUMMY1").
	  setFunction("''").
	  setHidden();
      
      headblk.addField("DUMMY2").
	  setFunction("''").
	  setHidden();

      headblk.addField("DUMMY3").
	  setFunction("''").
	  setHidden();

      
      headset = headblk.getASPRowSet();
      headbar = mgr.newASPCommandBar(headblk);
      
      headtbl = mgr.newASPTable(headblk);
      headtbl.setTitle(mgr.translate("DOCMAWISSUEMANSELECT: Configure Mandatory Search Fields"));
      
      
      headlay = headblk.getASPBlockLayout();
      headlay.setDefaultLayoutMode(headlay.CUSTOM_LAYOUT);
      headbar.disableMinimize();
   }


   public void  adjust()
   {
      
   }


//===============================================================
//  HTML
//===============================================================
   protected String getDescription()
   {
      return "DOCMAWISSUEMANSELECT: Configure Mandatory Search Fields";
   }

   protected String getTitle()
   {
      return "DOCMAWISSUEMANSELECT: Configure Mandatory Search Fields";
   }

   protected void printContents() throws FndException
   {
      ASPManager mgr = getASPManager();
      appendToHTML(headbar.showBar());
      beginDataPresentation();
      appendToHTML("<input type=\"hidden\" name=\"SELECTED_FIELDS\" value=\"\">\n");
      appendToHTML("<table border=\"0\" class=\"BlockLayoutTable\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");
      appendToHTML("<tr><td rowspan=\"3\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
      appendToHTML("<td>&nbsp;</td>");
      appendToHTML("<td></td>");
      appendToHTML("<td>&nbsp;</td>");
      appendToHTML("<td rowspan=\"3\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td></tr>");


      // Draw Available fields list box..
      appendToHTML("<tr><td nowrap>");
      appendToHTML(fmt.drawWriteLabel("DOCMAWISSUEMANSELECTAVAFIELD: Available Fields"));
      appendToHTML("<br>");
      appendToHTML(fmt.drawSelectStart("AVAILABLE_FIELDS", "style=\"width:225px;\" size=10 multiple"));

      // list of field..
      headset.first();
      for (int x = 0; x < headset.countRows(); x++)
      {
	  if ("0".equals(headset.getValue("SELECTED"))) 
	      appendToHTML(fmt.drawSelectOption(headset.getValue("CLIENT_FIELD"), headset.getValue("ASP_FIELD"), false));
	  headset.next();
      }

      appendToHTML(fmt.drawSelectEnd());
      appendToHTML("</td>");

      // Draw arrows for moving selections..
      appendToHTML("<td align=\"center\" width=\"100%\">");
      appendToHTML("<table border=\"0\">\n");
      appendToHTML("<tr><td align=\"center\"><a href=\"javascript:addFields()\"><img border=0 src=\"../common/images/portlet_right.gif\" ></a></td></tr>\n");
      appendToHTML("<tr><td>&nbsp;</td></tr>\n");
      appendToHTML("<tr><td>&nbsp;</td></tr>\n");
      appendToHTML("<tr><td align=\"center\"><a href=\"javascript:removeFields()\"><img border=0 src=\"../common/images/portlet_left.gif\" ></a></td></tr>\n");
      appendToHTML("</table>\n");
      appendToHTML("</td>\n");

      // Draw list box for selected fields..
      appendToHTML("<td nowrap>");
      appendToHTML(fmt.drawWriteLabel("DOCMAWISSUEMANSELECTFIELDSELECTED: Mandatory Fields to Query"));
      
      appendToHTML("<br>");
      appendToHTML(fmt.drawSelectStart("MANDATORY_FIELDS", "style=\"width:225px;\" size=10 multiple"));
      
      headset.first();
      for (int x = 0; x < headset.countRows(); x++)
      {
	  if ("1".equals(headset.getValue("SELECTED"))) 
	      appendToHTML(fmt.drawSelectOption(headset.getValue("CLIENT_FIELD"), headset.getValue("ASP_FIELD"), false));
	  headset.next();
      }


      appendToHTML(fmt.drawSelectEnd());
      appendToHTML("</td></tr>\n");
      appendToHTML("<tr><td>\n");
      appendToHTML("<br>\n");
      printButton("APPLYCLOSE","DOCMAWISSUEMANSELECTAPPCLOSE: Apply","onClick=\"javascript:apply('apply')\"\n");
      appendToHTML("&nbsp;&nbsp;&nbsp;");
      printButton("Close","DOCMAWISSUEMANSELECTAPPCANCEL: Close","onClick=\"window.close()\"\n");
      appendToHTML("</td>\n");
      appendToHTML("</tr>\n");
      appendToHTML("</table>\n");
      endDataPresentation(false);

      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("//----------------------------  CLIENT FUNCTIONS  -----------------------------\n");
      appendDirtyJavaScript("//-----------------------------------------------------------------------------\n");
      appendDirtyJavaScript("function apply()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var required = document.form.MANDATORY_FIELDS;\n");
      appendDirtyJavaScript("   var list = \"\";\n");
      appendDirtyJavaScript("   for (var x = 0; x < required.length; x++)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       list += required.options[x].value + '^'\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   if (list == \"\") \n");
      appendDirtyJavaScript("       list = '^'\n");
      appendDirtyJavaScript("   document.form.SELECTED_FIELDS.value = list;\n");
      appendDirtyJavaScript("   submit();\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function addFields()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var available = document.form.AVAILABLE_FIELDS;\n");
      appendDirtyJavaScript("   var required = document.form.MANDATORY_FIELDS;\n");
      appendDirtyJavaScript("   clearSelections(required);\n");
      appendDirtyJavaScript("   for (var x = 0; x < available.length; x++)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       if (available.options[x].selected)\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          insertOption(required, available.options[x].value, available.options[x].text);\n");
      appendDirtyJavaScript("          available.options[x] = null;\n");
      appendDirtyJavaScript("          --x;\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");


      appendDirtyJavaScript("function removeFields()\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var available = document.form.AVAILABLE_FIELDS;\n");
      appendDirtyJavaScript("   var required = document.form.MANDATORY_FIELDS;\n");
      appendDirtyJavaScript("   clearSelections(available);\n");
      appendDirtyJavaScript("   for (var x = 0; x < required.length; x++)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       if (required.options[x].selected)\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          insertOption(available, required.options[x].value, required.options[x].text);\n");
      appendDirtyJavaScript("          required.options[x] = null;\n");
      appendDirtyJavaScript("          --x;\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function clearSelections(select_list)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   for (var x = 0; x < select_list.length; x++)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       select_list.options[x].selected = false;\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("}\n");

      appendDirtyJavaScript("function insertOption(select_list, option_value, option_text)\n");
      appendDirtyJavaScript("{\n");
      appendDirtyJavaScript("   var new_option = document.createElement('option');\n");
      appendDirtyJavaScript("   new_option.value = option_value;\n");
      appendDirtyJavaScript("   new_option.appendChild(document.createTextNode(option_text));\n");
      appendDirtyJavaScript("   new_option.selected = true;\n");
      appendDirtyJavaScript("   option_text = option_text.toLowerCase();\n");
      appendDirtyJavaScript("   for (var x = 0; x < select_list.length; x++)\n");
      appendDirtyJavaScript("   {\n");
      appendDirtyJavaScript("       if (option_text < select_list.options[x].text.toLowerCase())\n");
      appendDirtyJavaScript("       {\n");
      appendDirtyJavaScript("          select_list.insertBefore(new_option, select_list.options[x]);\n");
      appendDirtyJavaScript("          return;\n");
      appendDirtyJavaScript("       }\n");
      appendDirtyJavaScript("   }\n");
      appendDirtyJavaScript("   select_list.insertBefore(new_option);\n");
      appendDirtyJavaScript("}\n");
   }

}
